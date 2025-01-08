$(document).ready(function() {
    countNotReadMessages();

    setInterval(countNotReadMessages, 1000);
});

function countNotReadMessages() {
    $.ajax({
        url: "/countNotReadMessages",
        type: "GET",
        success: function(count) {
            if (count > 0) {
                $('#notReadCount').text(count).show();
            } else {
                $('#notReadCount').hide();
            }
        },
        error: function() {
        }
    });
}