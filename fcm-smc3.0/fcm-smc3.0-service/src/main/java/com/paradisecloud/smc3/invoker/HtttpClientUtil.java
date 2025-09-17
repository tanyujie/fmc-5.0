package com.paradisecloud.smc3.invoker;

/**
 * @author nj
 * @date 2022/8/12 14:43
 */
public class HtttpClientUtil {

//    public static void main(String[] args) throws IOException {
//        HttpGet request = new HttpGet("http://172.16.100.117/sys-portal/tokens");
//
//        // 手动构建验证信息
//
//        String auth = "admin" + ":" + "admin@2021";
//
//        byte[] encodedAuth = Base64.encodeBase64(
//
//                auth.getBytes(StandardCharsets.UTF_8));
//
//        String authHeader = "Basic" + new String(encodedAuth);
//
//        // 将验证信息放入到 Header
//
//        request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
//        HttpClient client = HttpClientBuilder.create().build();
//
//        HttpResponse response = client.execute(request);
//        int statusCode = response.getStatusLine().getStatusCode();



//        RestTemplateBuilder builder = new RestTemplateBuilder();
//        RestTemplate restTemplate = builder.basicAuthentication("username", "password").build();
//        ResponseEntity<String> entity = restTemplate.getForEntity("http://172.16.100.117/sys-portal/tokens", String.class);
//        String msg = entity.getBody();


//    }
}
