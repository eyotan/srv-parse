<#import "parts/common.ftlh" as c>

<@c.page>

    <div class="container1">
        <form method="post" enctype="multipart/form-data" action="/upload">
            <label class="lbl">Разбор файла</label>
            <input type="file" name="file" required/>
            <button class="btn" type="submit">Загрузить</button>
        </form>
    </div>

</@c.page>