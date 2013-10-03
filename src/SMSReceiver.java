import java.util.Locale;

import android.content.*;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;


public class SMSReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (!intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED"))
			return;		// Not the intent we are looking for
		
		PluzSMSMessage message = parseMessage(context, intent);
		if (message == null) return;	// Something bad happened
		if (!isSpam(message)) return;	// Pass-through
		
		Log.i("filtrum", "Transfering parsed message to service");
		
		Intent smsIntent = new Intent(context.getApplicationContext(), SMSReceiverService.class);
		smsIntent.putExtra("message", message);
		context.startService(smsIntent);
	}

	private PluzSMSMessage parseMessage(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		if (bundle == null) return null;
		
		String sender = null, content = null;
		try {
			Object[] pdus = (Object[]) bundle.get("pdus");
			SmsMessage[] messages = new SmsMessage[pdus.length];
			
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < messages.length; i++) {
				messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				sender = messages[i].getOriginatingAddress();
				sb.append(messages[i].getMessageBody());
			}
			
			content = sb.toString();
		}
		catch (Exception e) {
			Log.e("filtrum", "failed to fetch SMS content", e);
		}
		
		if ((sender != null) && (content != null))
			return new PluzSMSMessage(sender, content);
		return null;
	}
	
	private boolean isSpam(PluzSMSMessage message) {
		// TODO
		return message.Content.toLowerCase(Locale.getDefault()).contains("louk");
	}
}
