#include <jni.h>
#include <string>
#include <fcntl.h>
#include <unistd.h>

extern "C" JNIEXPORT jstring

JNICALL
Java_com_example_myapplication_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {

    int fd;
    fd = open("/dev/fpga_led", O_RDWR);
    unsigned char val = '7';
    write(fd, &val, sizeof(val));
    close(fd);

    return env->NewStringUTF("Hello from C++");
}