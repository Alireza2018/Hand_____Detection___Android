#include <jni.h>
#include <string>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>

#include "opencv2/objdetect/objdetect.hpp"
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"

using namespace std;
using namespace cv;

extern "C"
JNIEXPORT jstring

JNICALL
Java_magnifier_oslomet_app_net_magnifier_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}


//Hand detector
void detector(Mat& frame);
void Hist_and_Backproj(int, void* );

/// Global Variables
//Mat src; Mat hsv; Mat hue;
//int bins = 25;
//
//JNIEXPORT void JNICALL Java_magnifier_oslomet_app_net_magnifier_MainActivity_backProjection
//        (JNIEnv *, jobject instance, jlong addrRgba) {
//    //get the frame
//    Mat& frame = *(Mat*)addrRgba;
//
//    detector(frame);
//}
//
//void detector(Mat& frame) {
//
//    cvtColor( src, hsv, CV_BGR2HSV );
//
//    hue.create( hsv.size(), hsv.depth() );
//
//    int ch[] = { 0, 0 };
//    mixChannels( &hsv, 1, &hue, 1, ch, 1 );
//    Hist_and_Backproj(0, 0);
//}
//
//void Hist_and_Backproj(int, void* ) {
//    MatND hist;
//    int histSize = MAX( bins, 2 );
//    float hue_range[] = { 0, 180 };
//    const float* ranges = { hue_range };
//
//    calcHist( &hue, 1, 0, Mat(), hist, 1, &histSize, &ranges, true, false );
//    normalize( hist, hist, 0, 255, NORM_MINMAX, -1, Mat() );
//
//    MatND backproj;
//    calcBackProject( &hue, 1, 0, hist, backproj, &ranges, 1, true );
//
//    int w = 400; int h = 400;
//    int bin_w = cvRound( (double) w / histSize );
//    Mat histImg = Mat::zeros( w, h, CV_8UC3 );
//
//    for( int i = 0; i < bins; i ++ ) {
//        rectangle( histImg, Point( i*bin_w, h ), Point( (i+1)*bin_w, h - cvRound( hist.at<float>(i)*h/255.0 ) ), Scalar( 0, 0, 255 ), -1 );
//    }
//}
