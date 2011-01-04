package org.waterforpeople.mapping.portal.client.widgets.component;

import com.gallatinsystems.framework.gwt.component.PageController;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;

public class AdminHomeWidget extends Composite implements ClickHandler {

	private static final String DESC_CSS = "description-text";
	private static final String BUTTION_CSS = "admin-button";

	private Button userMgmtButton;
	private Button surveyMgmtButton;
	private Button assignmentButton;
	private Button mappingButton;
	private PageController controller;
	
	public AdminHomeWidget(PageController controller) {
		Grid widget = new Grid(4, 2);
		this.controller = controller;
		userMgmtButton = initButton("Manage Users");

		widget.setWidget(0, 0, userMgmtButton);
		widget.setWidget(
				0,
				1,
				createDescription("Create, edit and delete user accounts for the dashboard."));
		surveyMgmtButton = initButton("Manage Surveys");
		widget.setWidget(1, 0, surveyMgmtButton);
		widget.setWidget(1, 1, createDescription("Create and publish surveys."));
		assignmentButton = initButton("Assign Surveys to Devices");
		widget.setWidget(2, 0, assignmentButton);
		widget.setWidget(
				2,
				1,
				createDescription("Assign surveys to remote devices for auto delivery."));
		mappingButton = initButton("Map Access PointAttributes");
		widget.setWidget(3, 0, mappingButton);
		widget.setWidget(
				3,
				1,
				createDescription("Map survey questions to fields in the Access Point. This is required before survey data will appear in maps."));
		initWidget(widget);
	}

	private Button initButton(String text) {
		Button button = new Button(text);
		button.addClickHandler(this);
		button.setStylePrimaryName(BUTTION_CSS);
		return button;
	}

	private Label createDescription(String text) {
		Label desc = new Label();
		desc.setStylePrimaryName(DESC_CSS);
		desc.setText(text);
		return desc;
	}

	@Override
	public void onClick(ClickEvent event) {
		if(event.getSource() == surveyMgmtButton){
			controller.openPage(SurveyGroupListWidget.class,null);
		}
	}

}