package com.example.jkaur3589.plantid;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.Timer;

//gps
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
//endgps

public class MainActivity extends AppCompatActivity implements LocationListener,View.OnTouchListener {

   // LocationManager myLocationManager;
   // String PROVIDER = LocationManager.GPS_PROVIDER;



    String gpsCoords = "";
    Timer timer = new Timer();

    Firebase myFirebaseRef;
    private static final String TAG = "Touch";
    @SuppressWarnings("unused")
    private static final float MIN_ZOOM = 1f,MAX_ZOOM = 1f;

    // These matrices will be used to scale points of the image
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    // The 3 states (events) which the user is trying to perform
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    // these PointF objects are used to record the point(s) the user is touching
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;

//gps
LocationManager myLocationManager;
    String PROVIDER = LocationManager.GPS_PROVIDER;
    //private static LocationService instance = null;

    private LocationManager locationManager;
    private final static int DISTANCE_UPDATES = 1;
    private final static int TIME_UPDATES = 1;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private boolean LocationAvailable;
    String mylo="43.3894077";
    String  myla="-80.4065496";
    //endgps

    private static String[] FilePathStrings;
    private static String[] FileNameStrings;
    private static File[] listFile;
    static File imageFile;
    GalleryImageAdapter galleryImageAdapter;
    ImageView imageView1;
    Menu menu;
    JSONArray jsonNameArr = new JSONArray();
    JSONArray jsonTypeArr = new JSONArray();
    JSONArray jsonLocationArr = new JSONArray();
    JSONArray jsonDetailsArr = new JSONArray();
    JSONArray jsonMsgArr = new JSONArray();

    String oldPhoto[] = new String[100];
    String newPhoto,st;

    public static ArrayList<ImageView> mImageIds = new ArrayList<ImageView>();
    //public static ImageView[] mImageIds = new ImageView[100];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Firebase.setAndroidContext(this);

        myFirebaseRef = new Firebase("https://blinding-inferno-6828.firebaseio.com/");

        myFirebaseRef.child("plants").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                //System.out.println(snapshot.getValue());  //prints "Do you have data? You'll love Firebase."
                String s = (String) snapshot.getValue();

                // textView = (TextView) findViewById(R.id.textView3);
                //textView.setText(s);

                try {
                    JSONObject obj = new JSONObject(s);
                    jsonNameArr = obj.getJSONArray("name");
                    jsonTypeArr = obj.getJSONArray("type");
                    jsonLocationArr = obj.getJSONArray("location");
                    jsonDetailsArr = obj.getJSONArray("detail");
                    jsonMsgArr = obj.getJSONArray("msgs");


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(FirebaseError error) {
            }

        });


        try{
            for (int i = 0; i < jsonTypeArr.length(); i++) {
                JSONObject json_data = jsonTypeArr.getJSONObject(i);
                oldPhoto[i] = json_data.toString();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        Log.v("dsa", String.valueOf(jsonTypeArr.length()));


        //final ImageView imageView = (ImageView)findViewById(R.id.imageView);
        final Gallery gallery = (Gallery) findViewById(R.id.gallery1);
        getImages();

        //Log.v("adsad", FilePathStrings[0]);

        gallery.setSpacing(1);
        galleryImageAdapter= new GalleryImageAdapter(this);
        gallery.setAdapter(galleryImageAdapter);
        galleryImageAdapter.notifyDataSetChanged();
        final Button button = (Button) findViewById(R.id.button2);
        imageView1 = (ImageView)findViewById(R.id.imageView);
        imageView1.setOnTouchListener(this);
       // imageView1.setClickable(true);

        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, final int position, long id) {

                Log.v("id", mImageIds.get(position).toString());
                //ImageView imageView = new ImageView(MainActivity.this);
                BitmapDrawable drawable = (BitmapDrawable) mImageIds.get(position).getDrawable();
                Bitmap bitmap = drawable.getBitmap();

                imageView1.setImageBitmap(bitmap);


                ByteArrayOutputStream bYtE = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bYtE);
                //bitmap.recycle();
                byte[] byteArray = bYtE.toByteArray();
                // final String imageFile = Base64.encodeToString(byteArray, Base64.DEFAULT);
                newPhoto = Base64.encodeToString(byteArray, Base64.DEFAULT);
                //newPhoto = imageFile;

                //imageView1 = mImageIds.get(position);

                Log.v("iamge", imageView1.toString());


                button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + FileNameStrings[position]);
                        Log.v("dsf", file.toString());

                        if (file.exists()) {
                            file.delete();
                            mImageIds.remove(position);
                            galleryImageAdapter.notifyDataSetChanged();
                            imageView1.setImageDrawable(null);
                            //galleryImageAdapter.notifyDataSetChanged();
                        }


                    }
                });


            }
        });



        /*imageView1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, PlantDetails.class);
                //myIntent.putExtra("name", FileNameStrings[position]); //Optional parameters
                myIntent.putExtra("name", "name");
                MainActivity.this.startActivity(myIntent);
            }
        });*/
        //gps
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (checkPermission()) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, TIME_UPDATES, DISTANCE_UPDATES, this);
        } else {
            requestPermission();
        }
    }//this line do not delete
    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED){
            LocationAvailable = true;
            return true;
        } else {
            LocationAvailable = false;
            return false;
        }
    }

    public void setImage(View v){

    }

    private void requestPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            Toast.makeText(this, "This app relies on location data for it's main functionality. Please enable GPS data to access all features.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    /**
                     * We are good, turn on monitoring
                     */
                    if (checkPermission()) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, TIME_UPDATES, DISTANCE_UPDATES, this);
                    } else {
                        requestPermission();
                    }
                } else {
                    /**
                     * No permissions, block out all activities that require a location to function
                     */
                    Toast.makeText(this, "Permission Not Granted.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public void onLocationChanged(Location arg0) {
        myla = String.valueOf(arg0.getLatitude());
        mylo = String.valueOf(arg0.getLongitude());
        Log.e("GPS", "location changed: lat=" + myla + ", lon=" + mylo);

        Toast.makeText(getApplicationContext(), "location changed: lat=" + myla + ", lon=" + mylo, Toast.LENGTH_LONG).show();

        // tv.setText("lat="+lat+", lon="+lon);
    }
    public void onProviderDisabled(String arg0) {
        Log.e("GPS", "provider disabled " + arg0);
    }
    public void onProviderEnabled(String arg0) {
        Log.e("GPS", "provider enabled " + arg0);
    }
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        Log.e("GPS", "status changed to " + arg0 + " [" + arg1 + "]");
    }

    //end gps!

    public void takePhoto(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, 11);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bundle extras = data.getExtras();
        Bitmap bmp = (Bitmap) extras.get("data");

        ByteArrayOutputStream bYtE = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, bYtE);
        //bmp.recycle();
        byte[] byteArray = bYtE.toByteArray();
        String imageFile = Base64.encodeToString(byteArray, Base64.DEFAULT);

        newPhoto = imageFile;

       // ImageView img = (ImageView) findViewById(R.id.imageView);
       // img.setImageBitmap((bmp));

        imageView1 = (ImageView) findViewById(R.id.imageView);
        imageView1.setImageBitmap(bmp);


        // We have a pointer to the image, now type cast it to String (firebase.com only accepts string data).


        saveToImageGallery(newPhoto);

        int i = 0;

        // Send to imageFile string to Firebase.com
      //  myFirebaseRef.child("FlowerTypeImage").setValue(imageFile);
       // myFirebaseRef.child("Details").setValue("name");
       // myFirebaseRef.child("message").setValue(gpsCoords);

       // MediaStore.Images.Media.insertImage(getContentResolver(), bmp, imageFile );

      /*  myFirebaseRef.child("FlowerTypeImage").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println(snapshot.getValue());  //prints "Do you have data? You'll love Firebase."

                //oldPhoto = (String)snapshot.getValue();

                // Log.i("mainactivity", snapshot.getValue().toString());

                // comparePhoto(imageFile, snapshot.getValue().toString());

                // saveToImageGallery(snapshot.getValue().toString());

            }

            @Override
            public void onCancelled(FirebaseError error) {
            }

        });*/

        //compare(newPhoto, oldPhoto);
    }

   // public void compare (String newPhoto, String[] oldPhoto){
    public void compare (View v){
      /*  imageView1 = (ImageView) findViewById(R.id.imageView);

        imageView1.buildDrawingCache();
        Bitmap bitmap = imageView1.getDrawingCache();*/

      //  ByteArrayOutputStream stream=new ByteArrayOutputStream();
       // bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
      //  byte[] image=stream.toByteArray();
       /// System.out.println("byte array:"+image);

       // String img_str = Base64.encodeToString(image, 0);


       // newPhoto = img_str;

   //     Log.e("photosasa", oldPhoto[0]);

        if(oldPhoto[0] == null ) {
            Log.e("photosasa", "newPhoto");
            Intent intent = new Intent(MainActivity.this, PlantDetails.class);
            intent.putExtra("name", newPhoto);
            startActivity(intent);
        }
        else
        {
            Log.e("photosasa", "oldPhoto");
            for(String s : oldPhoto){
                if(newPhoto.equals(s)){
                    Intent intent = new Intent(MainActivity.this, ShowDetails.class);
                    startActivity(intent);
                }
            }
        }
    /*    if(oldPhoto[0] != null || oldPhoto[0] != ""){
            Log.e("asa", oldPhoto[0]);
            for(String s : oldPhoto){
                if(newPhoto.equals(s)){
                    Intent intent = new Intent(MainActivity.this, ShowDetails.class);
                    startActivity(intent);
                }
            }
        }*/


     /*   Intent intent = new Intent(MainActivity.this, PlantDetails.class);
        intent.putExtra("name", newPhoto);
        startActivity(intent);*/

    }


    private void saveToImageGallery(String imageString)  {

      //  newPhoto = imageString;

        String filename = "myfile.jpg";
        String string = "Hello world!";
        FileOutputStream outputStream;
        Bitmap bitmap = null;

        try{
            byte [] encodeByte=Base64.decode(imageString, Base64.DEFAULT);
            bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            //return bitmap;
        }catch(Exception e){
            e.getMessage();
            //return null;
        }

//        File file = getAlbumStorageDir("JashanCollection");

        File filepath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//        File dir = new File(filepath.getAbsolutePath()+"/Mycollection/");
//        if (!dir.exists()) {
//            dir.mkdirs();
//        }

        Random r = new Random();
        File file = new File(filepath.getAbsolutePath()+"/"+r.nextInt()+".png");

        try {



        boolean isCreateFile =  file.createNewFile();
            if(isCreateFile) {

                outputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.flush();
                outputStream.close();

            }

         //   MediaStore.Images.Media.insertImage(getContentResolver(), bitmap,"z","z");



//            ContentValues values = new ContentValues();
//
//            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
//            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
//           // values.put(MediaStore.MediaColumns.DATA, filePath);

           // context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
           // outputStream.write(imageString.getBytes());


            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

            Uri contentUri = Uri.fromFile(file);

            Log.i("mainactivity", contentUri.toString());
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);

        } catch (Exception e) {
            e.printStackTrace();
        }

        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
        ImageView v = new ImageView(MainActivity.this);
        v.setImageBitmap(bitmap);

        mImageIds.add(v);
        galleryImageAdapter.notifyDataSetChanged();

    }

//    public File getAlbumStorageDir(String albumName) {
//        // Get the directory for the user's public pictures directory.
//        File file = new File(Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES), albumName);
//        if (!file.mkdirs()) {
//            Log.e("MainActivity", "Directory not created");
//        }
//        return file;
//
//
//    }
// Declare variables


    public void getImages()
    {
        // Check for SD Card
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {

        } else {
            // Locate the image folder in your SD Card
            imageFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES); ;
            // Create a new folder if no folder named SDImageTutorial exist
            imageFile.mkdirs();
        }

        if (imageFile.isDirectory()) {
            listFile = imageFile.listFiles();
            if(imageFile.listFiles() != null){
              //  Log.v("len", String.valueOf(listFile.length));
                // Create a String array for FilePathStrings
                FilePathStrings = new String[listFile.length];
                // Create a String array for FileNameStrings
                FileNameStrings = new String[listFile.length];

                for (int i = 0; i < listFile.length; i++) {
                    // Get the path of the image file
                    FilePathStrings[i] = listFile[i].getAbsolutePath();

                    Bitmap bitmap = BitmapFactory.decodeFile(listFile[i].getAbsolutePath());
                    ImageView view = new ImageView(MainActivity.this);
                    view.setImageBitmap(bitmap);

                    mImageIds.add(view);
                    mImageIds.removeAll(Collections.singleton(null));
                    // Get the name image file
                    FileNameStrings[i] = listFile[i].getName();
                }
            }

        }
    }

    public static void addImageToGallery(final String filePath, final Context context) {


    }


    private void delete(String file_dj_path){

        File fdelete = new File(file_dj_path);
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                System.out.println("file Deleted :" + file_dj_path);
            } else {
                System.out.println("file not Deleted :" + file_dj_path);
            }
        }
    }

    public class GalleryImageAdapter extends BaseAdapter
    {
        private Context mContext;



        public GalleryImageAdapter(Context context)
        {
            mContext = context;
        }

        public int getCount() {
            return mImageIds.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }


        // Override this method according to your need
        public View getView(int index, View view, ViewGroup viewGroup)
        {
            // TODO Auto-generated method stub
            ImageView i = new ImageView(mContext);
            i = mImageIds.get(index);
            //i.setImageResource(getImageId(MainActivity.this, FilePathStrings[index]));
           // i.setImageResource( FilePathStrings[index]);
            i.setLayoutParams(new Gallery.LayoutParams(200, 200));

            i.setScaleType(ImageView.ScaleType.FIT_XY);

            return i;
        }



    }

    public static int getImageId(Context context, String imageName) {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {

        } else {
            // Locate the image folder in your SD Card


            imageFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            // Create a new folder if no folder named SDImageTutorial exist
            imageFile.mkdirs();

        }
        return context.getResources().getIdentifier(imageFile +"/"+ imageName, null, context.getPackageName());
        //return context.getResources().getIdentifier(imageName, null, context.getPackageName());
    }
    // ZoomIn and Zoom Out Image

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        ImageView view = (ImageView) v;
        view.setScaleType(ImageView.ScaleType.MATRIX);
        float scale;

        dumpEvent(event);
        // Handle touch events here...

        switch (event.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:   // first finger down only
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                Log.d(TAG, "mode=DRAG"); // write to LogCat
                mode = DRAG;
                break;

            case MotionEvent.ACTION_UP: // first finger lifted

            case MotionEvent.ACTION_POINTER_UP: // second finger lifted

                mode = NONE;
                Log.d(TAG, "mode=NONE");
                break;

            case MotionEvent.ACTION_POINTER_DOWN: // first and second finger down

                oldDist = spacing(event);
                Log.d(TAG, "oldDist=" + oldDist);
                if (oldDist > 5f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                    Log.d(TAG, "mode=ZOOM");
                }
                break;

            case MotionEvent.ACTION_MOVE:

                if (mode == DRAG)
                {
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY() - start.y); // create the transformation in the matrix  of points
                }
                else if (mode == ZOOM)
                {
                    // pinch zooming
                    float newDist = spacing(event);
                    Log.d(TAG, "newDist=" + newDist);
                    if (newDist > 5f)
                    {
                        matrix.set(savedMatrix);
                        scale = newDist / oldDist; // setting the scaling of the
                        // matrix...if scale > 1 means
                        // zoom in...if scale < 1 means
                        // zoom out
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
        }

        view.setImageMatrix(matrix); // display the transformation on screen

        return true; // indicate event was handled
    }

    /*
     * --------------------------------------------------------------------------
     * Method: spacing Parameters: MotionEvent Returns: float Description:
     * checks the spacing between the two fingers on touch
     * ----------------------------------------------------
     */

    private float spacing(MotionEvent event)
    {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt(x * x + y * y);
    }

    /*
     * --------------------------------------------------------------------------
     * Method: midPoint Parameters: PointF object, MotionEvent Returns: void
     * Description: calculates the midpoint between the two fingers
     * ------------------------------------------------------------
     */

    private void midPoint(PointF point, MotionEvent event)
    {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /** Show an event in the LogCat view, for debugging */
    private void dumpEvent(MotionEvent event)
    {
        String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE","POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
        StringBuilder sb = new StringBuilder();
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        sb.append("event ACTION_").append(names[actionCode]);

        if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP)
        {
            sb.append("(pid ").append(action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
            sb.append(")");
        }

        sb.append("[");
        for (int i = 0; i < event.getPointerCount(); i++)
        {
            sb.append("#").append(i);
            sb.append("(pid ").append(event.getPointerId(i));
            sb.append(")=").append((int) event.getX(i));
            sb.append(",").append((int) event.getY(i));
            if (i + 1 < event.getPointerCount())
                sb.append(";");
        }

        sb.append("]");
        Log.d("Touch Events ---------", sb.toString());
    }

}



