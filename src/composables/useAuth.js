import { ref, onMounted, onUnmounted } from 'vue';
import { authState } from '@/utils/auth';

export function useAuth() {
    const isLoggedIn = ref(authState.isLoggedIn);

    const updateAuthState = (loggedIn) => {
        isLoggedIn.value = loggedIn;
    };

    onMounted(() => {
        authState.addListener(updateAuthState);
    });

    onUnmounted(() => {
        authState.removeListener(updateAuthState);
    });

    const updateLoginStatus = () => {
        authState.update();
    };

    return {
        isLoggedIn,
        updateLoginStatus
    };
}