// DataTables 테이블의 모든 헤더와 행 데이터를 JSON으로 변환하여 반환하는 함수
function getDataTableDataAsJson(tableId) {
    var table = $(tableId).DataTable();

    // 테이블 헤더 가져오기
    var headers = [];
    $(tableId + ' thead th').each(function () {
        headers.push($(this).text().trim());
    });

    // 테이블 행 데이터 가져오기
    var tableData = [];
    table.rows().every(function () {
        var rowData = {};
        var row = this.data();

        // 각 열 데이터를 헤더와 매칭시켜 JSON 형태로 변환
        headers.forEach(function (header, index) {
            rowData[header] = row[index];
        });

        tableData.push(rowData);
    });

    return tableData;
}

// DataTables 테이블의 데이터를 JSON으로 변환하여 서버로 전송하는 함수
function sendDataTableDataToServer(tableId, url) {
    var data = getDataTableDataAsJson(tableId);

    $.ajax({
        url: url,
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(data),
        xhrFields: {
            responseType: 'blob', // 서버에서 받은 응답이 엑셀 파일이므로 blob 형태로 처리
        },
        success: function (response) {
            const url = window.URL.createObjectURL(new Blob([response]));
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', 'Exported_Data.xlsx'); // 다운로드할 파일 이름 설정
            document.body.appendChild(link);
            link.click();
            link.remove();
        },
        error: function () {
            alert('데이터 전송에 실패했습니다.');
        }
    });
}
