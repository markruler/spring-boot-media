/**
 * chunk file 비동기 전송 전처리
 */
async function send_file_chunks_async() {
    show_loading();

    // TODO: Promise.all
    // await send_next_file_chunk_async(total_chunks, current_chunk, chunk_size, file);
}

/**
 * chunk file 비동기 전송
 * @param total_chunks
 * @param current_chunk
 * @param chunk_size
 * @param file
 */
async function send_next_file_chunk_async(total_chunks, current_chunk, chunk_size, file) {
    await fetch("/chunk/upload-async", {
        method: "post",
        body: formData
    }).then(resp => {
    }).catch(err => {
    });
}
