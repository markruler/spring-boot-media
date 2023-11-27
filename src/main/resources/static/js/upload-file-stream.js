/**
 * 파일 업로드
 */
function send_video_stream() {
    show_loading();

    const file = document.getElementById("video-file").files[0];
    const result_element = document.getElementById("result");

    fetch("/stream/upload", {
        method: "post",
        body: file
    }).then(resp => {
        if (resp.status === 200) {
            resp.text().then(data => result_element.textContent = data);
            hide_loading();
        }
    }).catch(err => {
        console.error("Error uploading video chunk", err);
        hide_loading();
    });
}
