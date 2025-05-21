// @charset "UTF-8";

const getFetch = async (url, param, isXml) => {
    try {
        const queryString = new URLSearchParams(param).toString();
        const response = await fetch(url + "?" + queryString);
        let result = "";
        if (isXml) {
            result = await response.text();
        } else {
            result = await response.json();
        }
        console.log("요청 URL: " + url, param, result);
        return result;
    } catch (e) {
        console.log(e);
        throw e;
    }
};
/**
 * post 처리를 위한 메서드로 
 */
const postFetch = async (url,body) => {
    try {
        const response = await fetch(url, {
            method: "post",
            headers:{
                "Content-Type":"application/x-www-form-urlencoded",    
            },            
            body: body
        });
        
        let result =  await response.json();
        console.log("요청 URL: " + url, body, result);
        return result;
    } catch (e) {
        console.log(e);
        throw e;
    }
};


// access token 가져오기;
const getAccessToken = async () => {
    try {
        const json = await getFetch("https://sgisapi.kostat.go.kr/OpenAPI3/auth/authentication.json", {
            consumer_key: key_sgis_service_id, // 서비스 id
            consumer_secret: key_sgis_security, // 보안 key
        });
        localStorage.setItem("SGIS_ACCESS_TOKEN", json.result.accessToken);
    } catch (e) {
        console.log(e);
    }
};

// 주소를 UTM-K좌표로 변환해서 반환: - json의 errCd ==-401에서 access token 확보!!
const getCoords = async (address) => {
    try {
        const json = await getFetch("https://sgisapi.kostat.go.kr/OpenAPI3/addr/geocode.json", {
            accessToken: localStorage.getItem("SGIS_ACCESS_TOKEN"),
            address: address,
            resultcount: 1,
        });
        if (json.errCd === -401) {
            await getAccessToken();
            return await getCoords(address);
        } else if (json.errMsg !== 'Success') {
            return json;
        } else {
            return json.result.resultdata[0];
        }
    } catch (e) {
        console.log(e);
    }
};

// 지도 정보에 주소 업데이트
const updateMap = (map, infos) => {
  const bounds = [];
  try {
    map.mks.forEach(marker => marker.remove() ); // 기존 마커 지우기
    for (let i = 0; i < infos.length; i++) {
      const info = infos[i];
      const marker = sop.marker([info.x, info.y]);
      marker.addTo(map).bindInfoWindow(info.title);
      map.mks.push(marker);
      bounds.push([info.x, info.y]);
    }
    // 경계를 기준으로 map을 중앙에 위치하도록 함
    if (bounds.length > 1) {
      map.setView(map._getBoundsCenterZoom(bounds).center, map._getBoundsCenterZoom(bounds).zoom);
    } else {
      map.setView(map._getBoundsCenterZoom(bounds).center, 9);
    }
  } catch (e) {
    console.log(e);
  }
};
