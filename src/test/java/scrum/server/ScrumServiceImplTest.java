package scrum.server;

import ilarkesto.base.Str;
import ilarkesto.persistence.AEntity;

import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import scrum.TestUtil;
import scrum.client.DataTransferObject;
import scrum.server.admin.User;
import scrum.server.project.Project;

public class ScrumServiceImplTest extends Assert {

	ScrumWebApplication app;
	ScrumServiceImpl service;
	WebSession session;
	GwtConversation conversation;
	User duke;

	@BeforeTest
	public void init() {
		TestUtil.initialize();
		app = TestUtil.getApp();

		duke = app.getUserDao().getUserByName("duke");
		if (duke == null) duke = app.getUserDao().postUser("duke", "geheim");

		service = new ScrumServiceImpl();
		app.autowire(service);

		session = (WebSession) app.createWebSession(null);
		session.setUser(duke);

	}

	@BeforeMethod
	public void initConversation() {
		conversation = (GwtConversation) session.createGwtConversation();
	}

	@Test
	public void loginFail() {
		service.onLogin(conversation, "duke", "bad password");
		assertConversationError("Login failed.");
	}

	@Test
	public void loginSuccess() {
		service.onLogin(conversation, "duke", "geheim");
		assertConversationWithoutErrors();
	}

	@Test
	public void createExampleProject() {
		service.onCreateExampleProject(conversation);
		Project project = getEntityByType(Project.class);
		assertTrue(project.getLabel().startsWith("Example Project"));
		assertTrue(project.containsAdmin(duke));
		assertTrue(project.containsParticipant(duke));
		assertTrue(project.containsProductOwner(duke));
		assertTrue(project.containsScrumMaster(duke));
		assertTrue(project.containsTeamMember(duke));
	}

	private <E extends AEntity> E getEntityByType(Class<E> type) {
		DataTransferObject dto = conversation.getNextData();
		if (!dto.containsEntities()) return null;
		for (Map entity : dto.getEntities()) {
			if (type.getSimpleName().toLowerCase().equals(entity.get("@type"))) {
				String id = (String) entity.get("id");
				E e = (E) app.getDaoService().getEntityById(id);
				return e;
			}
		}
		return null;
	}

	private <E extends AEntity> E createEntity(Class<E> type, Map properties) {
		E entity;
		try {
			entity = type.newInstance();
		} catch (InstantiationException ex) {
			throw new RuntimeException(ex);
		} catch (IllegalAccessException ex) {
			throw new RuntimeException(ex);
		}
		entity.updateProperties(properties);
		return entity;
	}

	private void assertConversationError(String error) {
		List<String> errors = conversation.getNextData().getErrors();
		assertTrue(errors != null && errors.contains(error),
			"Conversation error not found: <" + error + "> in " + Str.format(errors));
	}

	private void assertConversationWithoutErrors() {
		List<String> errors = conversation.getNextData().getErrors();
		assertTrue(errors == null || errors.isEmpty(), "Conversation contains errors: " + Str.format(errors));
	}
}
