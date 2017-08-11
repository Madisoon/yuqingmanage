<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <script src="https://code.jquery.com/jquery-3.2.1.min.js"></script>
    <title>新闻浏览</title>
    <style>
        #new-dialog {
            margin-left: 100px;
        }
    </style>
</head>
<body>
<div id="new-dialog">
${content}
</div>
<script>
    var widthMax = (window.screen.width - 200) * 2;
    $('#new-dialog').css({
        width: widthMax;
    });
</script>
</body>
</html>