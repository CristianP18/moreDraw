// useClickOutside.js
import { onMounted, onUnmounted, ref } from 'vue';

export function useClickOutside(elementRef) {
    const isClickedOutside = ref(false);

    function handleClickOutside(event) {
        if (elementRef.value && !elementRef.value.contains(event.target)) {
            isClickedOutside.value = true;
        } else {
            isClickedOutside.value = false;
        }
    }

    onMounted(() => {
        document.addEventListener('click', handleClickOutside);
    });

    onUnmounted(() => {
        document.removeEventListener('click', handleClickOutside);
    });

    return { isClickedOutside };
}
