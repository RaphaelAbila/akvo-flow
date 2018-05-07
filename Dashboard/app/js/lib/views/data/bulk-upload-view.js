/*global Resumable, FLOW, $, Ember */

FLOW.uuid = function (file) {
  return Math.uuidFast();
};

FLOW.uploader = Ember.Object.create({
  r: new Resumable({
    target: FLOW.Env.flowServices + '/upload',
    uploadDomain: FLOW.Env.surveyuploadurl.split('/')[2],
    simultaneousUploads: 1,
    testChunks: false,
    throttleProgressCallbacks: 1, // 1s
    chunkRetryInterval: 1000, // 1s
    chunkSize: 512 * 1024, // 512KB,
    generateUniqueIdentifier: FLOW.uuid
  }),

  assignDrop: function (el) {
    return this.get('r').assignDrop(el);
  },

  assignBrowse: function (el) {
    return this.get('r').assignBrowse(el);
  },

  support: function () {
    return this.get('r').support;
  }.property(),

  upload: function () {
    return this.get('r').upload();
  },

  pause: function () {
    return this.get('r').pause();
  },

  isUploading: function () {
    return this.get('r').isUploading();
  },

  cancel: function () {
    return this.get('r').cancel();
  },

  addFile: function (file) {
    return this.get('r').addFile(file);
  },

  bulkUpload: null,

  registerEvents: function () {
    var r = this.get('r');

    // Handle file add event
    r.on('fileAdded', function (file) {
      var li = $('#resumable-file-'+file.uniqueIdentifier);
      FLOW.uploader.set('cancelled', false);

      // Show progress pabr
      $('.resumable-list').show();
      if (li.length === 0) {
        $(".resumable-list").append("<li id='resumable-file-"+ file.uniqueIdentifier + "'></li>").scrollTop($('.resumable-list').outerHeight(true));
      }
      // Add the file to the list
      if (file.file.type !== "application/zip" && file.file.type !== "application/x-zip-compressed" && FLOW.uploader.get('bulkUpload')) {
        $("#resumable-file-"+ file.uniqueIdentifier).html(
          "<span class='resumable-file-name'>"+file.fileName+"</span>"
                +  Ember.String.loc('_unsupported_file_type')
                + "<img src='images/infolnc.png' class='unsupportedFile uploadStatus'> ");
        $("#resumable-file-"+ file.uniqueIdentifier).css({
            color: '#FF0000'});
        r.removeFile(file); //remove file
      } else {
        $("#resumable-file-"+ file.uniqueIdentifier).html(
          '<span class="resumable-file-name">'+file.fileName+'</span>'
          +'<span id="resumable-file-progress-'+file.uniqueIdentifier+'" class="uploadStatus"></span>'
          +'<div id="progress-bar-'+file.uniqueIdentifier+'" class="progress-bar"></div>').css('position','sticky');

        $('#progress-bar-'+file.uniqueIdentifier).css({
          width: '0%'
        });
        // Actually start the upload
        r.upload();
      }
    });

    r.on('pause', function () {
      // Show resume, hide pause
      $('.resumable-progress .progress-resume-link').show();
      $('.resumable-progress .progress-pause-link').hide();
    });

    r.on('complete', function () {
      // Hide pause/resume when the upload has completed
      $('.resumable-progress .progress-resume-link, .resumable-progress .progress-pause-link').hide();

      if (!FLOW.uploader.get('bulkUpload') && !FLOW.uploader.get('cancelled')) {
        FLOW.uploader.showCompleteMessage();
      }
    });

    r.on('fileSuccess', function (file, message) {
      var target = this.opts.target,
      data = {
        uniqueIdentifier: file.uniqueIdentifier,
        filename: file.fileName,
        baseURL: location.protocol + '//' + location.host,
        appId: FLOW.Env.appId,
        uploadDomain: this.opts.uploadDomain,
        complete: true
      },
      fname = file.fileName,
      excel = /\.xlsx$/gi,
      csv = /\.csv$/gi,
      sc;

      if (excel.test(fname)) {
        data.surveyId = FLOW.selectedControl.selectedSurvey.get('id');
      }

      if (csv.test(fname)) {
        sc = FLOW.selectedControl.selectedCascadeResource;
        data.cascadeResourceId = sc.get('keyId');
        data.numLevels = FLOW.selectedControl.get('cascadeImportNumLevels');
        data.includeCodes = !!FLOW.selectedControl.get('cascadeImportIncludeCodes');
      }

      // Reflect that the file upload has completed
      $("#resumable-file-"+ file.uniqueIdentifier).html(
        '<span class="resumable-file-name">'+file.fileName+'</span>'
        +'<img src = "images/tickBox.svg" class = "uploadComplete uploadStatus">'
      ).slideDown( "slow", function() {});
      setTimeout(function() {
        $.ajax({
          url : target,
          cache : false,
          type : 'POST',
          data : data
        });
      }, 500);

    });

    r.on('fileError', function (file, message) {
      // Reflect that the file upload has resulted in error
      $('#resumable-file-progress-'+file.uniqueIdentifier).html('('+ Ember.String.loc('_file_could_not_upload')+': ' + message + ')');
    });

    r.on('fileProgress', function (file) {
      // Handle progress for both the file and the overall upload
      $('#resumable-file-progress-'+file.uniqueIdentifier).html(Ember.String.loc('_uploading')+Math.floor(file.progress() * 100) + '%');
      $('#progress-bar-'+file.uniqueIdentifier).css({
        width: Math.floor(r.progress() * 100) + '%'
      });
    });

  },
  showCancelledMessage: function () {
    FLOW.dialogControl.set('activeAction', 'ignore');
    FLOW.dialogControl.set('header', Ember.String.loc('_upload_cancelled'));
    FLOW.dialogControl.set('message', Ember.String.loc('_upload_cancelled_due_to_navigation'));
    FLOW.dialogControl.set('showCANCEL', false);
    FLOW.dialogControl.set('showDialog', true);
  },
  showCompleteMessage: function () {
    FLOW.dialogControl.set('activeAction', 'ignore');
    FLOW.dialogControl.set('header', Ember.String.loc('_upload_complete'));
    FLOW.dialogControl.set('message', Ember.String.loc('_upload_complete_message'));
    FLOW.dialogControl.set('showCANCEL', false);
    FLOW.dialogControl.set('showDialog', true);
  }
});

FLOW.BulkUploadAppletView = FLOW.View.extend({
  didInsertElement: function () {
    FLOW.uploader.assignDrop($('.resumable-drop')[0]);
    FLOW.uploader.assignBrowse($('.resumable-browse')[0]);
    FLOW.uploader.registerEvents();
  },
  willDestroyElement: function () {
    FLOW.uploader.set('cancelled', FLOW.uploader.isUploading());
    FLOW.uploader.cancel();
    this._super();
  }
});

/* Show warning when trying to close the tab/window with an upload process in progress */
window.onbeforeunload = function (e) {
  var confirmationMessage = Ember.String.loc('_upload_in_progress');

  if (FLOW.uploader.isUploading()) {
    (e || window.event).returnValue = confirmationMessage;
    return confirmationMessage;
  }
};
