package templatemaching.app.alireza.com.templatemaching;

import android.content.Context;
import android.util.AttributeSet;
import android.hardware.Camera;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import org.opencv.android.JavaCameraView;



public class CustomSurfaceView extends JavaCameraView {

    private static final String TAG = "OPEN_CUSTOM_SURFACE_VIEW";
    protected SeekBar mSeekBar;

    public CustomSurfaceView(Context context, int cameraId) {
        super(context, cameraId);
    }


    public CustomSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setZoomControl(SeekBar _seekBar) {
        mSeekBar = _seekBar;
    }

    protected void enableZoomControls(Camera.Parameters params)
    {

        final int maxZoom = params.getMaxZoom();
        mSeekBar.setMax(maxZoom);
        mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            int progressValue = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                progressValue = progress;
                Camera.Parameters params = mCamera.getParameters();
                params.setZoom(progress);
                mCamera.setParameters(params);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }
        });
    }


    protected boolean initializeCamera(int width, int height)
    {

        boolean ret = super.initializeCamera(width, height);


        Camera.Parameters params = mCamera.getParameters();

        if(params.isZoomSupported())
            enableZoomControls(params);

        mCamera.setParameters(params);

        return ret;
    }
}
