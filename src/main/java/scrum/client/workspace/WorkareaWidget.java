package scrum.client.workspace;

import ilarkesto.gwt.client.AWidget;
import scrum.client.common.PanelWidget;
import scrum.client.impediments.ImpedimentListWidget;
import scrum.client.project.ProductBacklogWidget;
import scrum.client.project.ProjectOverviewWidget;
import scrum.client.sprint.SprintBacklogWidget;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class WorkareaWidget extends AWidget {

	private SimplePanel wrapper = new SimplePanel();
	private Widget currentWidget; // TODO AWidget

	private ProjectOverviewWidget projectOverview;
	private SprintBacklogWidget sprintBacklog;
	private ProductBacklogWidget productBacklog;
	private ImpedimentListWidget impedimentList;

	@Override
	protected Widget onInitialization() {
		currentWidget = new Label("workarea");
		wrapper = new SimplePanel();
		wrapper.add(currentWidget);
		return wrapper;
	}

	@Override
	protected void onUpdate() {
		wrapper.setWidget(new PanelWidget("Content", currentWidget));
		if (currentWidget != null && currentWidget instanceof AWidget) {
			((AWidget) currentWidget).update();
		}
	}

	public void showProjectOverview() {
		show(getProjectOverview());
	}

	public void showSprintBacklog() {
		show(getSprintBacklog());
	}

	public void showProductBacklog() {
		show(getProductBacklog());
	}

	public void showImpedimentList() {
		show(getImpedimentList());
	}

	private void show(Widget widget) {
		currentWidget = widget;
		Ui.get().unlock();
		update();
	}

	public ProjectOverviewWidget getProjectOverview() {
		if (projectOverview == null) projectOverview = new ProjectOverviewWidget();
		return projectOverview;
	}

	public SprintBacklogWidget getSprintBacklog() {
		if (sprintBacklog == null) sprintBacklog = new SprintBacklogWidget();
		return sprintBacklog;
	}

	public ProductBacklogWidget getProductBacklog() {
		if (productBacklog == null) productBacklog = new ProductBacklogWidget();
		return productBacklog;
	}

	public ImpedimentListWidget getImpedimentList() {
		if (impedimentList == null) impedimentList = new ImpedimentListWidget();
		return impedimentList;
	}

	public static WorkareaWidget get() {
		return WorkspaceWidget.get().getWorkarea();
	}
}