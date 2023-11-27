/**
 * Spinner 표시
 */
function show_loading(){
    console.log('show_loading');
    document.querySelector('.loading').style.display = 'block';
    document.querySelector('.dimmed').style.display = 'block';
}

/**
 * Spinner 숨김
 */
function hide_loading(){
    console.log('hide_loading');
    document.querySelector('.loading').style.display = 'none';
    document.querySelector('.dimmed').style.display = 'none';
}
