/**
 * 같은 파일을 여러개 업로드
 */
function send_file_multiply(count) {
    show_loading();

    send_next_file(0, count);
}

/**
 * <b>모바일 웹 브라우저</b>에서
 * 파일을 특정 사이즈로 자르는 것(chunking)이 오래 걸리는 걸로 추정돼서
 * 같은 파일을 여러개 업로드하는 방식 속도 테스트
 * -> chunking이 느리진 않음.
 */
function send_next_file(current, count) {
    const file = document.getElementById("video-file").files[0];

    fetch("/stream/upload", {
        method: "post",
        body: file
    }).then(resp => {
        if (resp.status === 200) {
            if (current < count) {
                send_next_file(current + 1, count);
                const result_element = document.getElementById("result");
                result_element.textContent = `${(current + 1) / count * 100}%`;
            } else {
                hide_loading();
            }
        }
    }).catch(err => {
        console.error("Error uploading video chunk", err);
        hide_loading();
    });

}
