#include <jvmti.h>
#include <stdio.h>

// функция, которая вызывается виртуальной машиной во время работы(налету)
JNIEXPORT jint JNICALL Agent_OnAttach(JavaVM* vm, char* options, void* reserved) {
    jvmtiEnv* jvmti;

    // Get JVMTI environment
    jint result = (*vm)->GetEnv(vm, (void**)&jvmti, JVMTI_VERSION_1);
    if (result != JNI_OK || jvmti == NULL) {
        printf("Unable to access JVMTI environment\n");
        return JNI_ERR;
    }

    char* vm_name = NULL;
    (*jvmti)->GetSystemProperty(jvmti, "java.vm.name", &vm_name);

    printf("Agent loaded. JVM name = %s\n", vm_name);
    fflush(stdout);

    return 0;
}