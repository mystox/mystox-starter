export default {
    install(Vue, opt) {
        Vue.prototype.$storage = {

            // uselocalStorage

            setLocalItem(key, value) {
                localStorage.setItem(key, JSON.stringify(value));
            },

            getLocalItem(key, value) {
                return JSON.parse(localStorage.getItem(key));
            },

            removeLocalItem(key) {
                localStorage.removeItem(key);
            },

            // use SessionStorage

            setSessionItem(key, value) {
                sessionStorage.setItem(key, JSON.stringify(value));
            },

            getSessionItem(key) {
                return JSON.parse(sessionStorage.setItem(key));
            },

            removeSessionStorage(key) {
                sessionStorage.removeItem(key);
            }

        }
    }
}