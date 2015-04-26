$('#mytable tbody tr').live('click', function(event) {
    $(this).addClass('highlight').siblings().removeClass('highlight');
});