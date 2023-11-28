/**
 * Spinner 표시
 */
function show_loading(){
    console.debug('show_loading');
    const result_element = document.getElementById("result");
    result_element.textContent = "0%";

    document.querySelector('.loading').style.display = 'block';
    document.querySelector('.dimmed').style.display = 'block';
}

/**
 * Spinner 숨김
 */
function hide_loading(){
    console.debug('hide_loading');
    setTimeout(() => {
        document.querySelector('.loading').style.display = 'none';
        document.querySelector('.dimmed').style.display = 'none';
    }, 500);
}
