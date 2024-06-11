#include <string>
#include <jni.h>
#include <cstdio>
#include <cstdlib>
#include <fcntl.h>
#include <unistd.h>
#include <termios.h>
#include <sys/mman.h>
#include <cerrno>

#define TEXTLCD_BASE            0xbc
#define TEXTLCD_COMMAND_SET     _IOW(TEXTLCD_BASE,0,int)
#define TEXTLCD_FUNCTION_SET    _IOW(TEXTLCD_BASE,1,int)
#define TEXTLCD_DISPLAY_CONTROL _IOW(TEXTLCD_BASE,2,int)
#define TEXTLCD_CURSOR_SHIFT    _IOW(TEXTLCD_BASE,3,int)
#define TEXTLCD_ENTRY_MODE_SET  _IOW(TEXTLCD_BASE,4,int)
#define TEXTLCD_RETURN_HOME     _IOW(TEXTLCD_BASE,5,int)
#define TEXTLCD_CLEAR           _IOW(TEXTLCD_BASE,6,int)
#define TEXTLCD_DD_ADDRESS      _IOW(TEXTLCD_BASE,7,int)
#define TEXTLCD_WRITE_BYTE      _IOW(TEXTLCD_BASE,8,int)

struct strcommand_varible {
    char rows;
    char nfonts;
    char display_enable;
    char cursor_enable;

    char nblink;
    char set_screen;
    char set_rightshit;
    char increase;
    char nshift;
    char pos;
    char command;
    char strlength;
    char buf[16];
};


static strcommand_varible strcommand;
static int initialized = 0;

void initialize()
{
    if(!initialized)
    {
        strcommand.rows = 0;
        strcommand.nfonts = 0;
        strcommand.display_enable = 1;
        strcommand.cursor_enable = 0;
        strcommand.nblink = 0;
        strcommand.set_screen = 0;
        strcommand.set_rightshit = 1;
        strcommand.increase = 1;
        strcommand.nshift = 0;
        strcommand.pos = 10;
        strcommand.command = 1;
        strcommand.strlength = 16;
        initialized = 1;
    }
}

extern "C" JNIEXPORT jint

JNICALL
Java_com_example_myapplication_MainActivity_TextLCDOut(JNIEnv* env, jobject thiz, jstring data0, jstring data1)
{
    jboolean iscopy;
    const char *buf0, *buf1;
    int fd, ret;

    fd = open("/dev/fpga_textlcd", O_WRONLY | O_NDELAY);
    if(fd < 0) return -errno;

    initialize();

    buf0 = env->GetStringUTFChars(data0, &iscopy);
    buf1 = env->GetStringUTFChars(data1, &iscopy);

    strcommand.pos = 0;
    ioctl(fd, TEXTLCD_DD_ADDRESS, &strcommand, 32);
    ret = write(fd, buf0, strlen(buf0));

    strcommand.pos = 40;
    ioctl(fd, TEXTLCD_DD_ADDRESS, &strcommand, 32);
    ret = write(fd, buf1, strlen(buf1));

    close(fd);

    env->ReleaseStringUTFChars(data0, buf0);
    env->ReleaseStringUTFChars(data1, buf1);

    return ret;
}