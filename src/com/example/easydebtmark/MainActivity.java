package com.example.easydebtmark;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final int REQUEST_CODE = 1234;
	private ListView wordsList;
	private DebtDatabase debtDB;
	private UnspentMoneyDatabase umDB;
	private List<DebtPerson> mainlist;
	private List<String> stringRelatedToNumber;

	/**
	 * Called with the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainpage);
		/*
		stringRelatedToNumber.add("one");
		stringRelatedToNumber.add("two");
		stringRelatedToNumber.add("three");
		stringRelatedToNumber.add("four");
		stringRelatedToNumber.add("five");
		stringRelatedToNumber.add("six");
		stringRelatedToNumber.add("seven");
		stringRelatedToNumber.add("eight");
		stringRelatedToNumber.add("nine");
		stringRelatedToNumber.add("ten");
		stringRelatedToNumber.add("eleven");
		stringRelatedToNumber.add("twelve");
		stringRelatedToNumber.add("thirteen");
		stringRelatedToNumber.add("fourteen");
		stringRelatedToNumber.add("fifteen");
		stringRelatedToNumber.add("sixteen");
		stringRelatedToNumber.add("seventeen");
		stringRelatedToNumber.add("eighteen");
		stringRelatedToNumber.add("nineteen");
		stringRelatedToNumber.add("twenty");
		stringRelatedToNumber.add("thirty");
		stringRelatedToNumber.add("forty");
		stringRelatedToNumber.add("fifty");
		stringRelatedToNumber.add("sixty");
		stringRelatedToNumber.add("seventy");
		stringRelatedToNumber.add("eighty");
		stringRelatedToNumber.add("ninty");
		stringRelatedToNumber.add("hundred");
		stringRelatedToNumber.add("thousand");
		stringRelatedToNumber.add("million");
		stringRelatedToNumber.add("billion");
		stringRelatedToNumber.add("trillion");
		*/
		openDatabase();

		Button speakButton = (Button) findViewById(R.id.speakButton);

		// Disable button if no recognition service is present PackageManager
		PackageManager pm = getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (activities.size() == 0) {
			speakButton.setEnabled(false);
			speakButton.setText("Recognizer not present");
		}

	}

	private void openDatabase() {
		debtDB = new DebtDatabase(this);
		umDB = new UnspentMoneyDatabase(this);

	}

	private void initializeMainList() {
		// Read data from database
		BuildMainList();

		// Set Click Event on list MAYBE
		//
	}

	private void BuildMainList() {
		Cursor cursor_umDB = umDB.getAllRows();
		if (cursor_umDB.moveToFirst()) {
			do {
				// Process the data:
				String name = cursor_umDB
						.getString(UnspentMoneyDatabase.COL_NAME);
				float totalowed = cursor_umDB
						.getFloat(UnspentMoneyDatabase.COL_TOTALOWED);
				float totalpaid = cursor_umDB
						.getFloat(UnspentMoneyDatabase.COL_TOTALPAID);
				mainlist.add(new DebtPerson(name, totalowed, totalpaid));
			} while (cursor_umDB.moveToNext());

			ArrayAdapter<DebtPerson> test = new MainDebtAdapter(mainlist);
		}

	}

	/**
	 * Handle the action of the button being clicked
	 */
	public void speakButtonClicked(View v) {
		startVoiceRecognitionActivity();
	}

	/**
	 * Fire an intent to start the voice recognition activity.
	 */
	private void startVoiceRecognitionActivity() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
				"Voice recognition Demo...");
		startActivityForResult(intent, REQUEST_CODE);
	}

	/**
	 * Handle the results from the voice recognition activity.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
			// Populate the wordsList with the String values the recognition
			// engine thought it heard
			ArrayList<String> matches = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			// TODO: Popup a list of possible options then let user decide (FOR NOW IT ALWAYS CHOOSE THE FIRST ONE)
			String selectedString = matches.get(0);
			Toast.makeText(this, selectedString, Toast.LENGTH_LONG).show();

			// TODO: PARSE the selected string (NAME AMOUNT DETAILS)
			String[] section = selectedString.split(" ");
			String name;
			float moneyOwed;
			String details;

			String date;
			// TODO: Then input it to both database (new entry for debtDB,
			// update for umDB)

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public class MainDebtAdapter extends ArrayAdapter<DebtPerson> {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View debtPersonView = convertView;
			if (debtPersonView == null) {
				debtPersonView = getLayoutInflater().inflate(
						R.layout.mainpage_list, parent, false);
			}
			DebtPerson currentDebtPerson = mainlist.get(position);

			TextView nameText = (TextView) debtPersonView
					.findViewById(R.id.mainlist_name);
			nameText.setText(currentDebtPerson.getName());

			TextView remainDebtText = (TextView) debtPersonView
					.findViewById(R.id.mainlist_remainDebtText);
			float remainderDebt = currentDebtPerson.getTotalOwed()
					- currentDebtPerson.getTotalPaid();
			remainDebtText.setText("" + remainderDebt);

			// PAY/EDIT BUTTON CODE HERE
			//
			//
			//

			return debtPersonView;
		}

		public MainDebtAdapter(List<DebtPerson> list) {
			super(MainActivity.this, R.layout.mainpage_list, list);
		}

	}

}
