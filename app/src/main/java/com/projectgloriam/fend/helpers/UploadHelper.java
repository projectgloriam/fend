package com.projectgloriam.fend.helpers;

import static android.content.ContentValues.TAG;

import static androidx.camera.core.ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY;
import static androidx.camera.core.ImageCapture.FLASH_MODE_AUTO;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.Size;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraInfo;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class UploadHelper {
    private Fragment fragment;
    private String url;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    String currentPhotoPath;
    private ImageCapture imageCapture;

    public UploadHelper(Fragment fragment){
        this.fragment = fragment;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = fragment.getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void selectImage() {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
        Executor cameraExecutor =  Executors.newSingleThreadExecutor();
        AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo"))
                {
                    ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                            ProcessCameraProvider.getInstance(fragment.getActivity());

                    cameraProviderFuture.addListener(() -> {
                        try {
                            // Camera provider is now guaranteed to be available
                            ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                            // Set up the view finder use case to display camera preview
                            Preview preview = new Preview.Builder().build();

                            // Set up the capture use case to allow users to take photos
                            imageCapture = new ImageCapture.Builder()
                                    .setCaptureMode(CAPTURE_MODE_MAXIMIZE_QUALITY)
                                    .setFlashMode(FLASH_MODE_AUTO)
                                    .build();

                            // Choose the camera by requiring a lens facing
                            CameraSelector cameraSelector = new CameraSelector.Builder()
                                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                                    .build();

                            ImageAnalysis imageAnalysis =
                                    new ImageAnalysis.Builder()
                                            .setTargetResolution(new Size(1280, 720))
                                            .build();

                            // Attach use cases to the camera with the same lifecycle owner
                            cameraProvider.bindToLifecycle(fragment.getActivity(), cameraSelector, imageCapture, imageAnalysis, preview);

                            // Connect the preview use case to the previewView
                            //preview.setSurfaceProvider(previewView.getSurfaceProvider());
                            // Create the File where the photo should go
                            File photoFile = null;
                            try {
                                photoFile = createImageFile();
                            } catch (IOException ex) {
                                // Error occurred while creating the File
                            }
                            // Continue only if the File was successfully created
                            if (photoFile != null) {
                                ImageCapture.OutputFileOptions outputFileOptions =
                                        new ImageCapture.OutputFileOptions.Builder(photoFile).build();
                                imageCapture.takePicture(outputFileOptions, cameraExecutor,
                                        new ImageCapture.OnImageSavedCallback() {
                                            @Override
                                            public void onImageSaved(ImageCapture.OutputFileResults outputFileResults) {
                                                Intent takePictureIntent = new Intent();
                                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileResults.getSavedUri());
                                                fragment.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                                            }
                                            @Override
                                            public void onError(ImageCaptureException error) {
                                                // insert your code here.
                                                Log.d(TAG, "imageCaptureException:"+error);
                                            }
                                        }
                                );
                                /*Uri photoURI = FileProvider.getUriForFile(fragment.getActivity(),
                                        "com.projectgloriam.fend.fileprovider",
                                        photoFile);*/
                            }

                        } catch (InterruptedException | ExecutionException e) {
                            // Currently no exceptions thrown. cameraProviderFuture.get()
                            // shouldn't block since the listener is being called, so no need to
                            // handle InterruptedException.
                        }
                    }, ContextCompat.getMainExecutor(fragment.getActivity()));


                    /*try {
                        fragment.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    } catch (ActivityNotFoundException e) {
                        // display error state to the user
                        Toast.makeText(fragment.getContext(), "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
                    }*/
                }
                else if (options[item].equals("Choose from Gallery"))
                {
                    Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    fragment.startActivityForResult(intent, 2);
                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public Bitmap takePhoto(){
        Bitmap bitmap = null;
        File f = new File(Environment.getExternalStorageDirectory().toString());
        for (File temp : f.listFiles()) {
            if (temp.getName().equals("temp.jpg")) {
                f = temp;
                break;
            }
        }
        try {
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), bitmapOptions);
            bitmap=getResizedBitmap(bitmap, 400);
            BitMapToString(bitmap);
            String path = android.os.Environment
                    .getExternalStorageDirectory()
                    + File.separator
                    + "Phoenix" + File.separator + "default";
            f.delete();
            OutputStream outFile = null;
            File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
            try {
                outFile = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                outFile.flush();
                outFile.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public Bitmap chosePhoto(Intent data){
        try {
            final Uri imageUri = data.getData();
            final InputStream imageStream = fragment.getActivity().getContentResolver().openInputStream(imageUri);
            Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            selectedImage=getResizedBitmap(selectedImage, 400);
            return selectedImage;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(fragment.getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
        }
        return null;
    }

    public String BitMapToString(Bitmap userImage1) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        userImage1.compress(Bitmap.CompressFormat.PNG, 60, baos);
        byte[] b = baos.toByteArray();
        url = Base64.encodeToString(b, Base64.DEFAULT);
        return url;
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}
