package org.allen.ins.underwriting.rc.decision.util;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.util.StringUtils;

/**
 * Java调用Python HTTP API的通用工具类（适配Spring Boot 3.5.7）
 * 核心：基础地址配yml，支持手动传递请求方式（POST/GET/PUT等）+ 路径参数
 */
@Component
public class PythonApiHttpClient {
    // 从配置文件读取Python API基础地址（主机+端口，如http://127.0.0.1:8000）
    @Value("${python.risk.api.base-url:http://127.0.0.1}")
    private String pythonApiBaseUrl;

    // 超时配置
    @Value("${python.risk.api.connect-timeout:5000}")
    private int connectTimeout;

    @Value("${python.risk.api.read-timeout:10000}")
    private int readTimeout;

    // Spring容器管理的RestTemplate（单例）
    private RestTemplate restTemplate;

    /**
     * 初始化RestTemplate，配置超时（Spring Boot 3.x 兼容写法）
     */
    @PostConstruct
    public void initRestTemplate() {
        restTemplate = new RestTemplate();
        // 配置超时（避免匿名内部类警告，Spring 3.x 规范写法）
        org.springframework.http.client.SimpleClientHttpRequestFactory requestFactory =
                new org.springframework.http.client.SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeout);
        requestFactory.setReadTimeout(readTimeout);
        restTemplate.setRequestFactory(requestFactory);
    }

    /**
     * 通用调用Python API（支持手动指定请求方式、路径参数）
     * @param method     请求方式（如HttpMethod.POST/HttpMethod.GET）
     * @param path       API路径参数（如/risk/calculate）
     * @param request    请求参数实体类（GET请求可传null，POST/PUT需传）
     * @param responseType 响应实体类类型
     * @return  解析后的响应实体
     */
    public <T, R> R callPythonApi(HttpMethod method, String path, T request, Class<R> responseType) {
        // 1. 校验核心参数
        if (method == null) {
            throw new IllegalArgumentException("请求方式（HttpMethod）不能为空");
        }
        if (!StringUtils.hasText(path)) {
            throw new IllegalArgumentException("Python API路径参数不能为空（如/risk/calculate）");
        }

        // 2. 拼接完整API地址（处理斜杠兼容）
        String fullApiUrl = pythonApiBaseUrl + path;

        // 3. 构建JSON请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // 可选：添加认证/自定义头
        // headers.set("Authorization", "Bearer " + token);

        // 4. 封装请求体（兼容GET请求无请求体的场景）
        HttpEntity<T> httpEntity;
        if (request == null || HttpMethod.GET.equals(method)) {
            // GET请求：无请求体，仅传请求头
            httpEntity = new HttpEntity<>(headers);
        } else {
            // POST/PUT等：传请求头+请求体
            httpEntity = new HttpEntity<>(request, headers);
        }

        try {
            // 5. 发送请求（通用exchange方法，适配所有请求方式）
            ResponseEntity<R> response = restTemplate.exchange(
                    fullApiUrl,
                    method,
                    httpEntity,
                    responseType
            );

            // 6. 校验响应状态
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                throw new RuntimeException(
                        String.format("Python API返回非成功状态码：%s，响应体：%s",
                                response.getStatusCode(),
                                response.getBody())
                );
            }
        } catch (ResourceAccessException e) {
            // 连接超时/网络异常
            throw new RuntimeException("调用Python API失败：网络超时/无法连接", e);
        } catch (HttpClientErrorException e) {
            // 4xx错误（比如参数错误）
            throw new RuntimeException(
                    String.format("Python API返回客户端错误：%s，响应体：%s",
                            e.getStatusCode(),
                            e.getResponseBodyAsString()),
                    e
            );
        } catch (Exception e) {
            // 其他异常（JSON解析失败等）
            throw new RuntimeException("调用Python API失败：" + e.getMessage(), e);
        }
    }
}