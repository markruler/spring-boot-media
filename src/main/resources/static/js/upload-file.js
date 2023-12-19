/**
 * file 업로드
 */
function send_file() {
    show_loading();

    const file = document.getElementById("video-file").files[0];

    // form data 형식으로 전송
    const formData = new FormData();
    formData.append("file", file, file.name);

    fetch("/upload", {
        method: "post",
        body: formData
    }).then(resp => {
        const result_element = document.getElementById("result");
        if (resp.status === 200) {
            resp.text().then(data => result_element.textContent = data);
        }
    }).catch(err => {
        console.error("Error uploading video chunk", err);
    }).finally(() => {
        hide_loading();
    });
}
