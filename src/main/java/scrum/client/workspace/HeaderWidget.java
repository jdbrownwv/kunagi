package scrum.client.workspace;

import ilarkesto.core.scope.Scope;
import ilarkesto.gwt.client.DropdownMenuButtonWidget;
import ilarkesto.gwt.client.Gwt;
import ilarkesto.gwt.client.HyperlinkWidget;
import ilarkesto.gwt.client.TableBuilder;
import ilarkesto.gwt.client.SwitchingNavigatorWidget.SwitchAction;
import ilarkesto.gwt.client.undo.UndoButtonWidget;
import scrum.client.ApplicationInfo;
import scrum.client.ScrumScopeManager;
import scrum.client.admin.LogoutAction;
import scrum.client.common.AScrumAction;
import scrum.client.common.AScrumWidget;
import scrum.client.img.Img;
import scrum.client.project.ChangeProjectAction;
import scrum.client.project.Project;
import scrum.client.search.SearchInputWidget;
import scrum.client.undo.Undo;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class HeaderWidget extends AScrumWidget {

	private SimplePanel wrapper;
	private Label title;
	private UndoButtonWidget undoButton;
	private DropdownMenuButtonWidget switchProjectButton;
	private SearchInputWidget search;
	private CommunicationIndicatorWidget status;
	private HTML feedback;

	@Override
	protected Widget onInitialization() {
		setHeight100();

		title = new Label("");
		title.setStyleName("HeaderWidget-title");

		status = new CommunicationIndicatorWidget();

		undoButton = new UndoButtonWidget();

		switchProjectButton = new DropdownMenuButtonWidget();
		switchProjectButton.setLabel("Switch Project");

		search = new SearchInputWidget();

		feedback = new HTML("<a href='http://kunagi.org/support.html' target='blank'>Support/Feedback</a>");

		wrapper = Gwt.createDiv("HeaderWidget", title);
		return wrapper;
	}

	@Override
	protected void onUpdate() {
		boolean projectOpen = ScrumScopeManager.isProjectScope();

		undoButton.setUndoManager(null);

		ApplicationInfo applicationInfo = getApp().getApplicationInfo();
		if (applicationInfo != null) {
			title.setText(applicationInfo.getName());
			title.setTitle(applicationInfo.getVersionDescription());
		}

		if (projectOpen) {
			switchProjectButton.clear();
			switchProjectButton.addAction("ProjectSelectorLink", new AScrumAction() {

				@Override
				public String getLabel() {
					return "Project Selector";
				}

				@Override
				protected void onExecute() {
					getNavigator().gotoProjectSelector();
				}

			});
			switchProjectButton.addSeparator();
			for (Project p : getDao().getProjects()) {
				switchProjectButton.addAction("QuickLinks", new ChangeProjectAction(p));
			}
		}

		TableBuilder tb = new TableBuilder();
		tb.setCellPadding(2);
		tb.setColumnWidths("70px", "", "", "", "200px", "60px", "100px", "50px");
		Widget searchWidget = projectOpen ? search : Gwt.createEmptyDiv();
		Widget undoWidget = projectOpen ? undoButton : Gwt.createEmptyDiv();
		// Widget changeProjectWidget = projectOpen ? new HyperlinkWidget(new ChangeProjectAction()) : Gwt
		// .createEmptyDiv();
		Widget changeProjectWidget = projectOpen ? switchProjectButton : Gwt.createEmptyDiv();
		tb.add(createLogo(), status, createCurrentUserWidget(), searchWidget, feedback, undoWidget,
			changeProjectWidget, new HyperlinkWidget(new LogoutAction()));
		wrapper.setWidget(tb.createTable());

		super.onUpdate();
	}

	private Widget createCurrentUserWidget() {
		boolean loggedIn = getAuth().isUserLoggedIn();
		if (!loggedIn) return Gwt.createEmptyDiv();

		ProjectWorkspaceWidgets widgets = Scope.get().getComponent(ProjectWorkspaceWidgets.class);
		if (!ScrumScopeManager.isProjectScope()) return new Label(createCurrentUserText());

		SwitchAction action = widgets.getSidebar().getNavigator().createSwitchAction(widgets.getProjectUserConfig());
		action.setLabel(createCurrentUserText());
		return new HyperlinkWidget(action);
	}

	private String createCurrentUserText() {
		boolean loggedIn = getAuth().isUserLoggedIn();
		StringBuilder text = new StringBuilder();
		if (loggedIn) {
			text.append(getCurrentUser().getName());
			if (ScrumScopeManager.isProjectScope()) {
				text.append(getCurrentProject().getUsersRolesAsString(getCurrentUser(), " (", ")"));
				text.append(" @ " + getCurrentProject().getLabel());
				undoButton.setUndoManager(Scope.get().getComponent(Undo.class).getManager());
			}
		}
		return text.toString();
	}

	private Widget createLogo() {
		SimplePanel div = Gwt.createDiv("HeaderWidget-logo", Img.bundle.logo25().createImage());
		ApplicationInfo applicationInfo = getApp().getApplicationInfo();
		if (applicationInfo != null) div.setTitle(applicationInfo.getVersionDescription());
		return div;
	}

}
