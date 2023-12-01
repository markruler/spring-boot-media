/**
 * chunk file 전송 전처리
 */
function send_file_chunks() {
    show_loading();

    const ONE_MB = 1024 * 1024;
    const chunk_size = 10 * ONE_MB;
    if (debug_alert) alert(`(1) chunk size: ${chunk_size}`);
    const file = document.getElementById("video-file").files[0];
    if (debug_alert) alert(`(2) file size: ${file.size}`);

    // total size 계산
    const total_chunks = Math.ceil(file.size / chunk_size);
    let current_chunk = 0;
    if (debug_alert) alert(`(3) total chunks: ${total_chunks}`);

    // recursive
    send_next_file_chunk(total_chunks, current_chunk, chunk_size, file);
}

/**
 * chunk file 전송
 * @param total_chunks
 * @param current_chunk
 * @param chunk_size
 * @param file
 */
function send_next_file_chunk(total_chunks, current_chunk, chunk_size, file) {
    // chunk size 만큼 데이터 분할
    if (debug_alert) alert(`(4) recursive - current chunk: ${current_chunk}`)
    const start = current_chunk * chunk_size;
    const end = Math.min(start + chunk_size, file.size);

    if (debug_alert) alert(`(5) start: ${start}, end: ${end}`);
    document.querySelector("#chunk-start").textContent = `Chunking... ${start}`;
    console.log(`${new Date} chunking... ${end}`);
    const chunk = file.slice(start, end);
    console.log(`${new Date} chunking done...`);
    document.querySelector("#chunk-end").textContent = `Chunking Done ${end}`;

    // form data 형식으로 전송
    const formData = new FormData();
    formData.append("chunk", chunk, file.name);
    formData.append("chunkNumber", current_chunk);
    formData.append("totalChunks", total_chunks);
    if (debug_alert) alert(`(6) fetch...`);

    fetch("/chunk/upload", {
        method: "post",
        body: formData
    }).then(resp => {
        const result_element = document.getElementById("result");
        // 전송 결과가 206(Partial Content)이면 다음 파일 조각 전송
        if (resp.status === 206) {
            // 진행률 표시
            result_element.textContent =
                Math.floor(current_chunk / total_chunks * 100) + "%"

            current_chunk++;
            if (current_chunk < total_chunks) {
                send_next_file_chunk(total_chunks, current_chunk, chunk_size, file);
            }
            // 마지막 파일까지 전송 되면
        } else if (resp.status === 200) {
            resp.text().then(data => result_element.textContent = data);
            hide_loading();
        }
    }).catch(err => {
        console.error("Error uploading video chunk", err);
        hide_loading();
    });
}
