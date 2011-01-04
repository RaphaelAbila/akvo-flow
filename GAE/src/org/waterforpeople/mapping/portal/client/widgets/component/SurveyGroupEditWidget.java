package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.HashMap;
import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyServiceAsync;

import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.gallatinsystems.framework.gwt.wizard.client.CompletionListener;
import com.gallatinsystems.framework.gwt.wizard.client.ContextAware;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SurveyGroupEditWidget extends Composite implements ContextAware,
		ChangeHandler {

	private static final String FORM_LABEL_CSS = "form-label";
	private static final String TXT_BOX_CSS = "txt-box";
	private VerticalPanel panel;
	private Map<String, Object> bundle;
	private TextBox codeBox;
	private TextBox descriptionBox;
	private SurveyServiceAsync surveyService;
	private SurveyGroupDto currentDto;
	private boolean isChanged;

	public SurveyGroupEditWidget() {
		panel = new VerticalPanel();
		surveyService = GWT.create(SurveyService.class);
		codeBox = new TextBox();
		descriptionBox = new TextBox();
		panel.add(buildRow("Code: ", codeBox));
		panel.add(buildRow("Description: ", descriptionBox));
		currentDto = null;
		initWidget(panel);
	}

	private Widget buildRow(String label, TextBox box) {
		Label l = new Label();
		l.setText(label);
		l.setStylePrimaryName(FORM_LABEL_CSS);
		box.setStylePrimaryName(TXT_BOX_CSS);
		box.addChangeHandler(this);
		HorizontalPanel row = new HorizontalPanel();
		row.add(l);
		row.add(box);
		return row;
	}

	private void populateWidgets() {
		isChanged = false;
		if (currentDto != null) {
			codeBox.setText(currentDto.getCode());
			descriptionBox.setText(currentDto.getDescription());
		}
	}

	private boolean validateInput() {
		return ViewUtil.isTextPopulated(codeBox);
	}


	public void saveSurveyGroup(final CompletionListener listener) {
		if (validateInput()) {
			if (currentDto == null) {
				currentDto = new SurveyGroupDto();
			}
			currentDto.setCode(codeBox.getText().trim());
			currentDto
					.setDescription(descriptionBox.getText() != null ? descriptionBox
							.getText().trim() : null);
			final MessageDialog savingDialog = new MessageDialog("Saving...",
					"Please wait.", true);
			savingDialog.showRelativeTo(panel);
			surveyService.saveSurveyGroup(currentDto,
					new AsyncCallback<SurveyGroupDto>() {

						@Override
						public void onFailure(Throwable caught) {
							savingDialog.hide();
							MessageDialog errDia = new MessageDialog(
									"Could not save survey group",
									"There was an error while attempting to save the survey group. Please try again. If the problem persists, please contact an administrator");
							errDia.showRelativeTo(panel);
							if(listener != null){
								listener.operationComplete(false,getContextBundle());
							}

						}

						@Override
						public void onSuccess(SurveyGroupDto result) {
							currentDto = result;
							savingDialog.hide();
							if(listener != null){
								listener.operationComplete(true,getContextBundle());
							}

						}
					});
		} else {
			MessageDialog validationDialog = new MessageDialog(
					"Invalid Survey Group",
					"The survey group code must contain at least 1 character.");
			validationDialog.showRelativeTo(panel);
		}
	}

	@Override
	public void persistContext(CompletionListener listener) {
		if (isChanged) {
			saveSurveyGroup(listener);
		}
	}

	@Override
	public void setContextBundle(Map<String, Object> bundle) {
		this.bundle = bundle;
		currentDto = (SurveyGroupDto) bundle
				.get(BundleConstants.SURVEY_GROUP_KEY);
		populateWidgets();
	}

	@Override
	public Map<String, Object> getContextBundle() {
		if (bundle == null) {
			bundle = new HashMap<String, Object>();
		}
		bundle.put(BundleConstants.SURVEY_GROUP_KEY, currentDto);
		return bundle;
	}

	@Override
	public void onChange(ChangeEvent event) {
		if (currentDto != null) {
			if (currentDto.getCode() != null
					&& !currentDto.getCode().equals(codeBox.getText())) {
				isChanged = true;
			} else if (currentDto.getDescription() != null
					&& !currentDto.getDescription().equals(
							descriptionBox.getText())) {
				isChanged = true;
			} else if ((ViewUtil.isTextPopulated(codeBox) && currentDto.getCode() == null)
					|| (ViewUtil.isTextPopulated(descriptionBox) && currentDto
							.getDescription() == null)) {
				isChanged = true;
			} else {
				isChanged = false;
			}
		} else {
			// if we haven't saved, set isChanged to true if either field is non
			// empty
			isChanged = (ViewUtil.isTextPopulated(codeBox) || ViewUtil.isTextPopulated(descriptionBox));
		}
	}
}