import axios from "axios";

const BASEURL = "http://localhost:8080/api";

const api = axios.create({
    baseURL: BASEURL,
    timeout: 30 * 1000
});

export {
    api
}