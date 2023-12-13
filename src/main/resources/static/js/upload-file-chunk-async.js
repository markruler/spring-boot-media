/**
 * chunk file 비동기 전송 전처리
 */
async function send_file_chunks_async() {
    show_loading();

    // uploadId 발급
    const uploadId = await init_async_chunk_upload();
    console.debug(`uploadId: ${uploadId}`);

    const file = document.getElementById("video-file").files[0];
    const requests = await make_requests(uploadId, file);

    // 업로드 한 chunk 파일들의 etag 목록
    const etags = await Promise.all(
        requests
    ).then((res) => {
        console.debug(res);
        return res;
    }).catch(err => {
        console.error(err);
    });

    // 업로드 완료 후 알림 및 조합 처리
    await complete_upload_async(
        uploadId,
        etags,
        file.name.split('.').pop(),
    );
}

/**
 * uploadId 발급
 *
 * @returns {Promise<string>}
 */
async function init_async_chunk_upload() {
    return await fetch("/async/init").then((res) => res.text());
}

/**
 * chunk file을 비동기로 요청하기 위한 Promise 목록
 * @param uploadId
 * @param file
 * @returns {Promise<*[]>}
 */
async function make_requests(uploadId, file) {
    document.getElementById("current-progress").value = 0;

    const ONE_MB = 1024 * 1024;
    const chunk_size = 5 * ONE_MB;
    const total_chunks = Math.ceil(file.size / chunk_size);
    let current_index = 0;

    let requests = [];
    while (current_index < total_chunks) {
        const start = current_index * chunk_size;
        const chunk = file.slice(start, Math.min(start + chunk_size, file.size));
        requests.push(send_next_file_chunk_async(uploadId, current_index, total_chunks, chunk));
        current_index++;
    }

    return requests;
}

/**
 * chunk file 비동기 전송
 *
 * @param uploadId 업로드 식별자
 * @param current_index 현재 chunk index
 * @param chunk chunk file
 */
async function send_next_file_chunk_async(
    uploadId,
    current_index,
    total_chunks,
    chunk
) {
    const formData = new FormData();
    formData.append("uploadId", uploadId);
    // DB에 Etag와 index를 함께 저장한다면 Etag를 반환 받지만,
    // 간단한 데모를 위해 Etag를 반환 받지 않고 index만 반환 받는다.
    formData.append("index", current_index);
    formData.append("chunk", chunk, chunk.name);
    return fetch("/async/upload", {
        method: "post",
        body: formData
    }).then((resp) => {
        const previous_progress = document.getElementById("current-progress").value;
        // console.log(`previous_progress: ${previous_progress}`);
        const current_progress = Number(previous_progress) + 1;
        document.getElementById("current-progress").value = current_progress;
        // console.log(`progress: ${current_progress}`);
        let percent = Math.floor(current_progress / total_chunks * 100) + "%";
        // console.log(`percent: ${percent}`);
        document.getElementById("result").textContent = percent;

        return resp.text();
    });
}

async function complete_upload_async(
    uploadId,
    etags,
    fileExtension,
) {
    // await sleep(1_000);
    console.debug(`complete... uploadId: ${uploadId}`);

    return await
        fetch("/async/complete", {
            method: "post",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                "uploadId": uploadId,
                "etags": etags,
                "fileExtension": fileExtension,
            })
        }).then(res => {
            console.log(res);
        }).catch(err => {
            console.error(err);
        }).finally(() => {
            hide_loading();
        });
}

async function sleep(ms) {
    console.debug(`sleeping ${ms} ms...`);
    return new Promise((r) => setTimeout(r, ms));
}
