package youten.redo.ble.readwrite.AsyncTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import youten.redo.ble.readwrite.R;


/**
 * Created by Startup on 4/8/17.
 */

public abstract class MyAsyncTask extends AsyncTask<Void,Void,Void> {

    public static int TOAST=1;
    public static int DIALOG=2;
    public static int DIALOG_TITLE=3;
    public static int BAD=4;
    public static int START_INTENT=4;

    public int alertType=0;
    public Boolean isSucces=false;
    public String msg="";
    public String msgTitle="";
    public String smsg="";

    public String status;
    public ProgressDialog asyncDialog =new ProgressDialog(getContext());

    AlertDialog.Builder alert;



    @Override
    protected Void doInBackground(Void... params) {
        postData();
        return null;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        setPreloading();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        setPostLoading();
        if(isSucces) {

            setSuccessPostExecute();
        }
        else
        {
            setAlertFail();
            setFailPostExecute();
        }
    }

    public abstract Context getContext();


    public abstract void setSuccessPostExecute();
    public abstract void setFailPostExecute();
    public abstract void postData();

    public  void setPreloading()
    {
        asyncDialog.setMessage("Please wait...");
        asyncDialog.setCancelable(false);
        asyncDialog.show();
    }
    public void setPostLoading()
    {
        asyncDialog.dismiss();
    }

    public void setAlertFail()
    {
        if(alertType==TOAST)
        {
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        }
        else if(alertType==DIALOG)
        {
            alert = new AlertDialog.Builder(getContext());
            alert.setMessage(msg);
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            alert.show();

        }
        else if(alertType==DIALOG_TITLE)
        {
            alert = new AlertDialog.Builder(getContext());
            alert.setMessage(msg);
            alert.setTitle(msgTitle);
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            alert.show();

        }
        else  if (alertType==BAD)
        {
            alert = new AlertDialog.Builder(getContext());
            alert.setMessage(msg);
            if(!msgTitle.contentEquals("")) {
                alert.setTitle(msgTitle);
            }
            alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    ((Activity) (getContext())).finish();
                }
            });
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    ((Activity) (getContext())).finish();
                }
            });
            alert.show();
        }
        else
        {
            alert = new AlertDialog.Builder(getContext());
            alert.setMessage(msg);
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            alert.show();
        }
    }

    public void setAlertSuccess(String msgTitle, String msg )
    {
            alert = new AlertDialog.Builder(getContext());
            alert.setMessage(msg);
            alert.setTitle(msgTitle);
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            alert.show();
    }

    public void setAlertSuccess(String msg, int alertType )
    {
        if(alertType==TOAST)
        {
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        }
        else if(alertType==DIALOG)
        {
            alert = new AlertDialog.Builder(getContext());
            alert.setMessage(msg);
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            alert.show();

        }
    }

    public void setAlertSuccessClose( String msg )
    {

            alert = new AlertDialog.Builder(getContext());
            alert.setMessage(msg);
            alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    ((Activity) (getContext())).finish();
                }
            });
            alert.setPositiveButton("CLOSE", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    ((Activity) (getContext())).finish();

                }
            });
            alert.show();


    }

    public void setAlertSuccess(String msgTitle, String msg, final Intent intent, final Context context )
    {

            alert = new AlertDialog.Builder(getContext());
            alert.setMessage(msg);
            alert.setCancelable(false);
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    context.startActivity(intent);
                }
            });
            alert.show();
    }

    public void setSuccessAlert()
    {
        if(!smsg.contentEquals("")) {
            alert = new AlertDialog.Builder(getContext());

            alert.setTitle("Congrats!!");
            alert.setMessage(smsg);
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    ((Activity) (getContext())).finish();
                }
            });
            alert.show();
        }
    }

    public void badInternetAlert()
    {
        msgTitle= "no internet";
        msg="no internet";
        alertType=BAD;
    }

    public void badServerAlert()
    {
        msgTitle="error";
        msg="error";
        alertType=DIALOG_TITLE;
    }




}
