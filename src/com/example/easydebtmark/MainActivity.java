package com.example.easydebtmark;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
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
	private List<String> alertBuilderOptions;

	/**
	 * Called with the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainpage);
		/*
		 * stringRelatedToNumber.add("one"); stringRelatedToNumber.add("two");
		 * stringRelatedToNumber.add("three");
		 * stringRelatedToNumber.add("four"); stringRelatedToNumber.add("five");
		 * stringRelatedToNumber.add("six"); stringRelatedToNumber.add("seven");
		 * stringRelatedToNumber.add("eight");
		 * stringRelatedToNumber.add("nine"); stringRelatedToNumber.add("ten");
		 * stringRelatedToNumber.add("eleven");
		 * stringRelatedToNumber.add("twelve");
		 * stringRelatedToNumber.add("thirteen");
		 * stringRelatedToNumber.add("fourteen");
		 * stringRelatedToNumber.add("fifteen");
		 * stringRelatedToNumber.add("sixteen");
		 * stringRelatedToNumber.add("seventeen");
		 * stringRelatedToNumber.add("eighteen");
		 * stringRelatedToNumber.add("nineteen");
		 * stringRelatedToNumber.add("twenty");
		 * stringRelatedToNumber.add("thirty");
		 * stringRelatedToNumber.add("forty");
		 * stringRelatedToNumber.add("fifty");
		 * stringRelatedToNumber.add("sixty");
		 * stringRelatedToNumber.add("seventy");
		 * stringRelatedToNumber.add("eighty");
		 * stringRelatedToNumber.add("ninty");
		 * stringRelatedToNumber.add("hundred");
		 * stringRelatedToNumber.add("thousand");
		 * stringRelatedToNumber.add("million");
		 * stringRelatedToNumber.add("billion");
		 * stringRelatedToNumber.add("trillion");
		 */
		openDatabase();
		alertBuilderOptions = new ArrayList<String>();

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
		
		//TODO: Do these in separate thread. Thanks Android.
		debtDB.open();
		umDB.open();
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
			AlertDialog.Builder options = new AlertDialog.Builder(this);
			options.setTitle("");

			alertBuilderOptions.clear();
			CharSequence results[] = new CharSequence[matches.size()];
			for (int i = 0; i < matches.size(); i++) {
				results[i] = (CharSequence) matches.get(i);
				alertBuilderOptions.add(matches.get(i));
			}

			options.setItems(results, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// String selectedString = matches.get(which);
					// Toast.makeText(this, selectedString,
					// Toast.LENGTH_LONG).show();

					// String[] section = selectedString.split(" ");

					// FORMATS:
					// I owe/borrowed [name] [amount] for [details]
					// I owe/borrowed [amount] to [name] for [details]
					// [name] lend/lent (me) [amount] for [details]

					// String[] dataArray =
					// alertBuilderOptions.get(which).toLowerCase().split(" ");
					String[] dataArray = "Some shitty dude 150 dollars 30 cents for piece of crappy crap rap"
							.split(" ");

					// int indexName = 0;
					int indexMoney = 99;
					float indexMoneyCent = 1;
					int indexDetails = 99;

					String name = "";
					float moneyOwed = 0;
					String details = "";

					// if (dataArray[0].startsWith("i")) {
					// for (int i = 2; i < dataArray.length; i++) {
					// if (TextUtils
					// .isDigitsOnly((CharSequence) dataArray[i])) {
					// moneyOwed += Float.parseFloat(dataArray[i])
					// / indexMoneyCent;
					// indexMoneyCent = 100;
					// } else if (dataArray[i].startsWith("for")) {
					// indexDetails = i + 1;
					// } else if (dataArray[i].startsWith("cent")
					// || dataArray[i].startsWith("dollar")
					// || dataArray[i].startsWith("to")
					// || i >= indexDetails - 1) {
					// // DO NOTHING
					// } else {
					// name.concat(dataArray[i]);
					// }
					// }
					// for (int i = indexDetails; i < dataArray.length; i++) {
					// details.concat(dataArray[i]);
					// }
					// } else {
					// indexName = 0;
					// for (int i = 1; i < dataArray.length; i++) {
					// if (dataArray[i].startsWith("lend")
					// || dataArray[i].startsWith("lent")) {
					// indexMoney = i + 1;
					// } else if (dataArray[i].startsWith("for")) {
					// indexDetails = i + 1;
					// }
					// }
					//
					// for (int i = 0; i < dataArray.length; i++) {
					// if (i < indexMoney - 1) {
					// name.concat(dataArray[i]);
					// } else if (i == indexMoney - 1) {
					// // NOPE
					// } else if (i < indexDetails - 1) {
					// moneyOwed += Float.parseFloat(dataArray[i])
					// / indexMoneyCent;
					// indexMoneyCent = 100;
					// i++;
					// } else if (i == indexDetails - 1) {
					// // NOPE
					// } else {
					// details.concat(dataArray[i]);
					// }
					// }
					// }

					// Fuck that format, just go with "[name] [money] for
					// [details]"

					for (int i = 0; i < dataArray.length; i++) {
						if (TextUtils.isDigitsOnly((CharSequence) dataArray[i])
								&& indexMoney >= i) {
							indexMoney = i;
						} else if (dataArray[i].startsWith("for")) {
							i++;
							indexDetails = i;
						} else {
							// Nothing
						}
					}

					for (int i = 0; i < dataArray.length; i++) {
						if (i < indexMoney) {
							name += dataArray[i];
						} else if (i < indexDetails - 1
								&& (i + 1 < dataArray.length)
								&& (dataArray[i + 1].startsWith("dollar"))) {
							moneyOwed += Float.parseFloat(dataArray[i]);
							i++;
						} else if (i < indexDetails - 1
								&& TextUtils
										.isDigitsOnly((CharSequence) dataArray[i])) {
							moneyOwed += Float.parseFloat(dataArray[i]) / 100;
						} else if (i == indexDetails - 1) {
							// NOPE, skip "for" text
						} else if (i >= indexDetails) {
							details += dataArray[i];
						} else {
							// DO NOTHING
						}
					}

					DateFormat dateFormat = new SimpleDateFormat(
							"yyyy/MM/dd HH:mm:ss");
					Calendar cal = Calendar.getInstance();
					String date = dateFormat.format(cal.getTime());

					// TODO: Then input it to both database (new entry for
					// debtDB,
					// update for umDB)
					debtDB.insertRow(name, date, moneyOwed, 0, details);
					

				}
			});
			options.show();

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
