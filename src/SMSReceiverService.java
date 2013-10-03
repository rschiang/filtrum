import android.app.IntentService;
import android.content.*;
import android.database.*;
import android.net.Uri;
import android.os.Bundle;
import java.lang.Thread;
import android.util.Log;

public class SMSReceiverService extends IntentService {

	public SMSReceiverService() {
		super("com.pluzlab.filtrum.SMSReceiverService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle bundle = intent.getExtras();
		if (bundle == null) return;

		PluzSMSMessage message = (PluzSMSMessage) bundle.get("message");
		if (message == null) return;

		ContentResolver resolver = getContentResolver();
		for (int tries = 0; tries < 5; tries++) {
			if (tryAppendTag(resolver, message)) {
				Log.i("filtrum", "Tag successfully appended");
				break;
			} else
				try {
					Thread.sleep(200);	// Wait for 200ms and continue
				} catch (InterruptedException e) {}
		}
	}

	protected boolean tryAppendTag(ContentResolver resolver, PluzSMSMessage message) {
		Uri uri = Uri.parse("content://sms");
		Cursor c = resolver.query(uri, 
						new String[] {"_id", "address", "date", "body"}, 
						"address = ?", 
						new String[] { message.Sender }, 
						"date DESC");
		try {
			if (!c.moveToFirst()) {
				Log.w("filtrum", "SMS target not found");
				return false;
			}
			String id = c.getString(c.getColumnIndexOrThrow("_id"));
			String body = c.getString(c.getColumnIndexOrThrow("body"));
			ContentValues values = new ContentValues();
			values.put("body", "[!]" + body);
			resolver.update(uri, values, "_id = ?", new String[] { id });
			return true;
		}
		catch (Exception e) {
			Log.e("filtrum", "failed to append tag on SMS content", e);
			return false;
		}
	}
}