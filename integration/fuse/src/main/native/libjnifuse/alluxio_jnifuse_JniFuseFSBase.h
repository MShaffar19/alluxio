/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class alluxio_jnifuse_JniFuseFSBase */

#ifndef _Included_alluxio_jnifuse_JniFuseFSBase
#define _Included_alluxio_jnifuse_JniFuseFSBase
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     alluxio_jnifuse_JniFuseFSBase
 * Method:    doMount
 * Signature: (Ljava/lang/String;[Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_alluxio_jnifuse_JniFuseFSBase_doMount
  (JNIEnv *, jobject, jstring, jobjectArray);

/*
 * Class:     alluxio_jnifuse_JniFuseFSBase
 * Method:    fuse_main
 * Signature: (I[Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_alluxio_jnifuse_JniFuseFSBase_fuse_1main
  (JNIEnv *, jobject, jint, jobjectArray);

#ifdef __cplusplus
}
#endif
#endif