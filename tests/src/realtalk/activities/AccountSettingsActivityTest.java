package realtalk.activities;

import org.junit.Before;
import org.junit.Test;

import realtalk.controller.ChatControllerStub;
import realtalk.util.UserInfo;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

import com.jayway.android.robotium.solo.Solo;
import com.realtalk.R;

public class AccountSettingsActivityTest extends ActivityInstrumentationTestCase2<AccountSettingsActivity> {
	private Solo solo;
	
	public AccountSettingsActivityTest() {
		super(AccountSettingsActivity.class);
		ChatControllerStub.getInstance().fInitialize(new UserInfo("leeb92", "brandon", "aa"));
	}
	
	@Before
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
	}
	
	@Test
	public void testButtonsAndTextDisplay() {
		assertTrue(solo.searchButton("Submit"));
		assertTrue(solo.searchButton("Click Here to Delete Account"));
		assertFalse(solo.searchButton("WRONG_BUTTON"));
	}

	@Test
	public void testEmptyPassword() {
		EditText edittextOld = (EditText) solo.getView(R.id.oldpword);
		solo.enterText(edittextOld, " ");
		EditText edittextNew = (EditText) solo.getView(R.id.newpword);
		solo.enterText(edittextNew, "test");
		EditText edittextConf = (EditText) solo.getView(R.id.confpword);
		solo.enterText(edittextConf, " ");
		solo.clickOnButton("Submit");
		assertTrue("Could not find the dialog", solo.searchText("Please fill in all of the fields."));
		solo.clickOnButton("Close");
	} 
	 
	@Test
	public void testNonMatchingPasswords() {
		EditText edittextOld = (EditText) solo.getView(R.id.oldpword);
		solo.enterText(edittextOld, "yo");
		EditText edittextNew = (EditText) solo.getView(R.id.newpword);
		solo.enterText(edittextNew, "test");
		EditText edittextConf = (EditText) solo.getView(R.id.confpword);
		solo.enterText(edittextConf, "testt");
		solo.clickOnButton("Submit");
		assertTrue("Could not find the dialog", solo.searchText("New Password and Confirmation Password do not match.  Please try again."));
		solo.clickOnButton("Close");
	}
	
	@Test
	public void testPasswordTooLong() {
		EditText edittextOld = (EditText) solo.getView(R.id.oldpword);
		solo.enterText(edittextOld, "yo");
		EditText edittextNew = (EditText) solo.getView(R.id.newpword);
		solo.enterText(edittextNew, "abababjdfjlkffffffffffasdfjlkdjaflkdsjffdfd");
		EditText edittextConf = (EditText) solo.getView(R.id.confpword);
		solo.enterText(edittextConf, "abababjdfjlkffffffffffasdfjlkdjaflkdsjffdfd");
		solo.clickOnButton("Submit");
		assertTrue("Could not find the dialog", solo.searchText("Password must not exceed 20 characters.  Please try again."));
		solo.clickOnButton("Close");
	}
	
	@Test
	public void testDenyingDeleteAccount() {
		solo.clickOnButton("Click Here to Delete Account");
		solo.clickOnButton("No");
	}

}
