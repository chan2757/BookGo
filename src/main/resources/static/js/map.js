var map;
var allMarkers = [];
var publicMarkers = [];
var smallMarkers = [];
var currentLocationMarker = null;
var openInfowindow = null;
var startLat, startLng; // 출발지 좌표 전역 변수
var destinationLat = null, destinationLng = null; // 목적지 좌표 전역 변수
var currentTransportMode = 'car'; // 디폴트 값으로 자동차 모드
var currentRouteType = 'trafast'; // 디폴트 값으로 가장 빠른 경로
var currentRoutePolyline = null; // 현재 경로 폴리라인
var currentDestinationMarker = null; // 현재 목적지 마커

// 경로 요약 정보를 화면에 표시하는 함수
function displaySummary(summary) {
    const summaryElement = document.getElementById('route-summary');
    summaryElement.innerHTML = `
        <h3>경로 요약</h3>   
        <br/>
        <p><strong>총거리:</strong> ${Math.round(summary.distance / 1000)} 키로미터</p>
        <p><strong>예상 시간:</strong> ${Math.round(summary.duration / 60000)} 분</p>
        <p><strong>통행료:</strong> ${summary.tollFare} 원</p>
        <p><strong>택시 요금:</strong> ${summary.taxiFare} 원</p>
        <p><strong>연료비:</strong> ${summary.fuelPrice} 원</p>
    `;
}

// 경로 안내 정보를 화면에 표시하는 함수
function displayGuides(guides) {
    const guidesElement = document.getElementById('route-guides');
    guidesElement.innerHTML = guides.map((guide, index) => `
        <div>
            <p><strong>단계 ${index + 1}:</strong> ${guide.instruction}</p>
            <p>거리: ${guide.distance} 미터</p>
        </div>
    `).join('');
}

// 경로 안내 정보를 토글하는 함수 (열고 닫기)
function toggleRouteGuides() {
    const guidesElement = document.getElementById('route-guides');
    const toggleButton = document.getElementById('toggleButton');
    if (guidesElement.style.display === 'none' || guidesElement.style.display === '') {
        guidesElement.style.display = 'block';
        toggleButton.textContent = '상세 안내 접기';
    } else {
        guidesElement.style.display = 'none';
        toggleButton.textContent = '상세 안내보기';
    }
}

// 경로 안내 데이터를 업데이트하는 함수
function updateDirections(directions) {
    console.log('Directions data:', directions);
    // 경로 데이터가 올바른지 확인
    if (!directions || !directions.summary || !Array.isArray(directions.guides) || !Array.isArray(directions.path)) {
        console.error('Invalid route data:', directions);
        alert('경로 데이터를 불러올 수 없습니다.');
        return;
    }

    // 기존 경로 폴리라인이 있으면 제거
    if (currentRoutePolyline) {
        currentRoutePolyline.setMap(null);
    }

    // 새로운 경로 폴리라인을 지도에 그리기
    currentRoutePolyline = new naver.maps.Polyline({
        path: directions.path.map(coord => new naver.maps.LatLng(coord[0], coord[1])),
        strokeColor: '#5347AA',
        strokeWeight: 4,
        strokeOpacity: 0.8,
        strokeStyle: 'solid',
        map: map
    });

    // 경로의 범위를 맞추기 위해 지도 확대/축소 설정
    const bounds = new naver.maps.LatLngBounds();
    directions.path.forEach(coord => bounds.extend(new naver.maps.LatLng(coord[0], coord[1])));
    map.fitBounds(bounds);

    // 경로 요약 정보 및 안내 표시
    displaySummary(directions.summary);
    displayGuides(directions.guides);
}

// 목적지로 경로를 찾는 함수
function findRoute() {
    if (!currentLocationMarker) {
        alert('출발지를 먼저 설정해주세요.');
        return;
    }

    if (destinationLat === null || destinationLng === null) {
        alert('목적지를 설정해주세요.');
        return;
    }

    // 기존 마커를 숨기고 새로운 목적지 마커를 설정
    setMarkersVisibility(allMarkers, false);

    if (currentDestinationMarker) {
        currentDestinationMarker.setMap(null);
    }

    currentDestinationMarker = new naver.maps.Marker({
        position: new naver.maps.LatLng(destinationLat, destinationLng),
        map: map,
        icon: {
            content: `<div class="map-marker" style="color: blue; font-size: 60px;">📍</div>`,
            anchor: new naver.maps.Point(30, 60)
        }
    });

    // 경로 찾기 시도
    const attemptToFindRoute = () => {
        if (typeof window.updateDirections === 'function') {
            fetch(`/bookgo/map/get-directions?startLat=${startLat}&startLng=${startLng}&destLat=${destinationLat}&destLng=${destinationLng}&mode=${currentTransportMode}&option=${currentRouteType}`)
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`HTTP error! status: ${response.status}`);
                    }
                    return response.json();
                })
                .then(data => {
                    console.log("길찾기 응답 데이터:", data);
                    updateDirections(data); // 응답 데이터를 통해 경로 안내 업데이트
                })
                .catch(error => {
                    console.error("Error fetching directions:", error);
                    alert('경로 데이터를 불러올 수 없습니다.');
                });
        } else {
            setTimeout(attemptToFindRoute, 100);
        }
    };

    attemptToFindRoute();
}

// 지도를 초기화하고 도서관 마커를 설정하는 함수
function initMap(centerLat, centerLng, libraries) {
    var mapContainer = document.getElementById('map');
    var mapOption = {
        center: new naver.maps.LatLng(centerLat, centerLng),
        zoom: 15 // 줌 레벨을 15로 설정
    };

    map = new naver.maps.Map(mapContainer, mapOption);

    // 도서관 데이터를 받아와 마커를 생성
    libraries.forEach(function (library, index) {
      //  console.log(`도서관 ${index + 1} 데이터:`, library); // 도서관 데이터 확인용 로그

        // 실제 필드명을 사용하여 조건을 수정
        if (
            library &&
            library.lib_LATITUDE && // 필드 이름 수정
            library.lib_LONGITUDE && // 필드 이름 수정
            library.lib_LATITUDE !== 0 &&
            library.lib_LONGITUDE !== 0
        ) {
            var markerPosition = new naver.maps.LatLng(
                library.lib_LATITUDE, // 필드 이름 수정
                library.lib_LONGITUDE // 필드 이름 수정
            );

            var marker = new naver.maps.Marker({
                position: markerPosition,
                map: map,
                icon: {
                    content: `<div class="map-marker">${
                        library.lib_TYPE === '공공도서관' ? '📚' : '🏫'
                    }</div>`,
                    anchor: new naver.maps.Point(12, 36)
                }
            });

            allMarkers.push(marker);

            // 도서관 유형에 따라 마커를 분류
            if (library.lib_TYPE === '공공도서관') {
                publicMarkers.push(marker);
            } else if (library.lib_TYPE === '작은도서관') {
                smallMarkers.push(marker);
            }

            // 마커 클릭 시 인포윈도우 열기
            var infowindow = new naver.maps.InfoWindow({
                content: `
                    <div style="padding:10px;font-size:14px;">
                        <strong>${library.lib_NAME}</strong><br>
                        <strong>유형:</strong> ${
                    library.lib_TYPE || '유형 정보 없음'
                }<br>
                        <strong>전화번호:</strong> ${
                    library.lib_PHONENUMBER || '전화번호 없음'
                }<br>
                        <strong>대출 가능 여부:</strong> ${
                    library.lib_LOANAVAILABILITY ? '가능' : '불가능'
                }<br>
                        ${
                    library.lib_OPERATIONDETAILS ||
                    '운영정보 미공개'
                }<br>
                        ${
                    library.lib_WEBSITE
                        ? `<a href="${library.lib_WEBSITE}" target="_blank">자세히 보기</a>`
                        : '사이트 없음'
                }<br>
                        <button onclick="setDestinationAndFindRoute(${library.lib_LATITUDE}, ${library.lib_LONGITUDE})">길찾기</button><br>
                        <button onclick="closeInfowindow()">닫기</button>
                    </div>
                `
            });

            naver.maps.Event.addListener(marker, 'click', function () {
                if (openInfowindow) {
                    openInfowindow.close();
                }
                infowindow.open(map, marker);
                openInfowindow = infowindow;

                // 마커를 클릭하면 해당 도서관을 목적지로 설정
                destinationLat = library.lib_LATITUDE;
                destinationLng = library.lib_LONGITUDE;
            });
        } else {
           // console.warn('유효하지 않은 도서관 데이터:', library); // 유효하지 않은 도서관 데이터 로그
        }
    });

    // 지도 클릭 시 인포윈도우 닫기 - 유효성 검사 추가
    if (map) {
        naver.maps.Event.addListener(map, 'click', function () {
            if (openInfowindow) {
                openInfowindow.close();
                openInfowindow = null;
            }
        });
    } else {
        console.warn('지도 객체가 초기화되지 않았습니다.');
    }

    changeMarker('all');
}

// 목적지를 설정하고 경로를 찾는 함수
function setDestinationAndFindRoute(lat, lng) {
    destinationLat = lat;
    destinationLng = lng;
    findRoute();
}

// 마커의 가시성을 변경하는 함수
function changeMarker(type) {
    var allMenu = document.getElementById('allMenu');
    var publicMenu = document.getElementById('publicMenu');
    var smallMenu = document.getElementById('smallMenu');

    if (type === 'all') {
        allMenu.className = 'btn btn-outline-secondary active';
        publicMenu.className = 'btn btn-outline-secondary';
        smallMenu.className = 'btn btn-outline-secondary';
        setMarkersVisibility(allMarkers, true);
    } else if (type === 'public') {
        allMenu.className = 'btn btn-outline-secondary';
        publicMenu.className = 'btn btn-outline-secondary active';
        smallMenu.className = 'btn btn-outline-secondary';
        setMarkersVisibility(allMarkers, false);
        setMarkersVisibility(publicMarkers, true);
    } else if (type === 'small') {
        allMenu.className = 'btn btn-outline-secondary';
        publicMenu.className = 'btn btn-outline-secondary';
        smallMenu.className = 'btn btn-outline-secondary active';
        setMarkersVisibility(allMarkers, false);
        setMarkersVisibility(smallMarkers, true);
    }
}

// 특정 마커 그룹의 가시성을 설정하는 함수
function setMarkersVisibility(markers, visible) {
    markers.forEach(function(marker) {
        marker.setVisible(visible);
    });
}

// 사용자의 현재 위치를 가져오는 함수
function getCurrentLocation() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
            function(position) {
                startLat = position.coords.latitude;
                startLng = position.coords.longitude;
                setRedMarker(startLat, startLng);
                map.setCenter(new naver.maps.LatLng(startLat, startLng));
            },
            function(error) {
                alert('현위치를 가져올 수 없습니다.');
            }
        );
    } else {
        alert('이 브라우저에서는 Geolocation이 지원되지 않습니다.');
    }
}

// 현재 위치에 빨간 마커를 설정하는 함수
function setRedMarker(lat, lng) {
    if (currentLocationMarker) {
        currentLocationMarker.setMap(null);
    }
    currentLocationMarker = new naver.maps.Marker({
        position: new naver.maps.LatLng(lat, lng),
        map: map,
        icon: {
            content: `<div class="map-marker" style="color: red; font-size: 60px;">📍</div>`,
            anchor: new naver.maps.Point(30, 60)
        }
    });
    map.setCenter(new naver.maps.LatLng(lat, lng));
}

// 장소 자동완성 목록을 표시하는 함수
function showAutocomplete(query) {
    var autocompleteList = document.getElementById('autocompleteList');
    autocompleteList.innerHTML = '';

    if (query.length > 1) {
        fetch(`/bookgo/map/search-place?query=${query}`)
            .then(response => response.json())
            .then(data => {
                if (data.length > 0) {
                    data.forEach(function(place) {
                        var listItem = document.createElement('li');
                        listItem.className = 'list-group-item';
                        listItem.textContent = place.title.replace(/<[^>]*>?/gm, '');
                        listItem.onclick = function() {
                            selectPlace(place);
                        };
                        autocompleteList.appendChild(listItem);
                    });
                }
            })
            .catch(error => {
                console.error("Error fetching autocomplete data:", error);
            });
    }
}

// 선택된 장소를 사용하여 목적지를 설정하는 함수
function selectPlace(place) {
    var address = place.roadAddress || place.address;

    fetch(`/bookgo/map/search-address?query=${address}`)
        .then(response => response.json())
        .then(coordData => {
            if (coordData.lat && coordData.lng) {
                destinationLat = coordData.lat;
                destinationLng = coordData.lng;
                setRedMarker(destinationLat, destinationLng);
                map.setCenter(new naver.maps.LatLng(destinationLat, destinationLng));
            } else {
                alert('좌표를 찾을 수 없습니다.');
            }
        })
        .catch(error => {
            console.error("Error fetching coordinates:", error);
        });

    document.getElementById('searchInput').value = place.title.replace(/<[^>]*>?/gm, '');
    document.getElementById('autocompleteList').innerHTML = '';
}

// 주소 검색을 통해 위치를 설정하는 함수
function searchLocation() {
    var query = document.getElementById('searchInput').value;
    if (query) {
        fetch(`/bookgo/map/search-address?query=${query}`)
            .then(response => response.json())
            .then(data => {
                if (data.lat && data.lng) {
                    destinationLat = data.lat;
                    destinationLng = data.lng;
                    setRedMarker(destinationLat, destinationLng);
                    map.setCenter(new naver.maps.LatLng(destinationLat, destinationLng));
                } else {
                    alert('검색 결과가 없습니다.');
                }
            })
            .catch(error => {
                console.error("Error searching location:", error);
            });
    } else {
        alert('검색어를 입력하세요.');
    }
}

// 경로 유형과 이동 수단을 변경하여 경로를 업데이트하는 함수
function updateRoute() {
    currentTransportMode = document.getElementById('transportModeSelect').value;
    currentRouteType = document.getElementById('routeTypeSelect').value;

    if (destinationLat !== null && destinationLng !== null) {
        findRoute(); // 이동 수단 및 경로 유형 변경 시 동일한 목적지로 경로 요청
    } else {
        alert('목적지를 설정해 주세요.');
    }
}

// 지도 초기화 및 마커 재설정을 위한 함수
function resetMap() {
    if (currentDestinationMarker) {
        currentDestinationMarker.setMap(null);
        currentDestinationMarker = null;
    }

    setMarkersVisibility(allMarkers, true);
    map.setCenter(new naver.maps.LatLng(startLat, startLng));
}

// 열려 있는 인포윈도우를 닫는 함수
function closeInfowindow() {
    if (openInfowindow) {
        openInfowindow.close();
        openInfowindow = null;
    }
}

// 문서가 로드된 후 초기 설정을 수행하는 함수
document.addEventListener("DOMContentLoaded", function() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
            function(position) {
                startLat = position.coords.latitude;
                startLng = position.coords.longitude;
                initMap(startLat, startLng, []); // 사용자의 위치로 지도를 초기화합니다.
                fetchLibrariesAndInitMap(startLat, startLng); // 도서관 데이터를 불러오고 지도에 표시합니다.
            },
            function(error) {
                console.error('현위치를 가져올 수 없습니다:', error);
                // 위치를 가져올 수 없을 때는 서울시청을 기본값으로 사용합니다.
                initMap(37.5665, 126.9780, []); // 서울시청 위치로 지도를 초기화합니다.
                fetchLibrariesAndInitMap(37.5665, 126.9780); // 도서관 데이터를 불러오고 지도에 표시합니다.
            }
        );
    } else {
        console.error('이 브라우저에서는 Geolocation이 지원되지 않습니다.');
        initMap(37.5665, 126.9780, []); // 브라우저가 Geolocation을 지원하지 않는 경우 서울시청으로 설정
        fetchLibrariesAndInitMap(37.5665, 126.9780); // 도서관 데이터를 불러오고 지도에 표시합니다.
    }
});

// 도서관 데이터를 가져와 지도를 초기화하는 함수
function fetchLibrariesAndInitMap(centerLat, centerLng) {
    fetch('/bookgo/map/libraries')
        .then(response => response.json())
        .then(data => {
            console.log(`도서관 데이터 개수: ${data.length}`); // 도서관 데이터 개수를 출력

            // 도서관 수를 표시할 HTML 요소 찾기
            const libraryCountElement = document.getElementById('libraryCount');

            // libraryCountElement가 존재하는지 확인하고 textContent를 설정
            if (libraryCountElement) {
                libraryCountElement.textContent = `도서관 수: ${data.length}`;
            } else {
                console.warn('도서관 수를 표시할 요소(libraryCount)가 없습니다.');
            }

            initMap(centerLat, centerLng, data); // 도서관 데이터를 이용해 지도를 초기화합니다.
        })
        .catch(error => {
            console.error("도서관 데이터를 가져오는 중 에러 발생:", error);
        });
}
