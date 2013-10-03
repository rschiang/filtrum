import android.os.*;

public class PluzSMSMessage implements Parcelable {

	public String Sender = null;
	
	public String Content = null;
	
	public PluzSMSMessage() {
	}
	
	public PluzSMSMessage(String sender, String content) {
		Sender = sender;
		Content = content;
	}

	private PluzSMSMessage(Parcel source) {
		Sender = source.readString();
		Content = source.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(Sender);
		dest.writeString(Content);	
	}
	
	public static final Parcelable.Creator<PluzSMSMessage> CREATOR = new Parcelable.Creator<PluzSMSMessage>() {

		@Override
		public PluzSMSMessage createFromParcel(Parcel source) {
			return new PluzSMSMessage(source);
		}

		@Override
		public PluzSMSMessage[] newArray(int size) {
			return new PluzSMSMessage[size];
		}
		
	};
}
