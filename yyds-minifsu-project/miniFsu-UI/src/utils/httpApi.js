import { Loading } from "element-ui";
import apiList from './apiList';

let loading;
const openFullScreen = () => {
  loading = Loading.service({
    lock: true,
    text: "loading...",
    spinner: "el-icon-loading",
    background: "rgba(0, 0, 0, 0.7)"
  });
};
const closeFullScreen = () => {
  setTimeout(() => {
    loading.close();
  }, 500);
};

export default apiList(openFullScreen, closeFullScreen)
