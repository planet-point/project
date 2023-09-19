package project.backend.infra.oauth.naver;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import project.backend.domain.OauthServerType;
import project.backend.domain.User;
import project.backend.domain.client.OauthMemberClient;
import project.backend.infra.oauth.naver.client.NaverApiClient;
import project.backend.infra.oauth.naver.dto.NaverMemberResponse;
import project.backend.infra.oauth.naver.dto.NaverToken;

@Component
@RequiredArgsConstructor
public class NaverMemberClient implements OauthMemberClient {

    private final NaverApiClient naverApiClient;
    private final NaverOauthConfig naverOauthConfig;

    @Override
    public OauthServerType supportServer() {
        return OauthServerType.NAVER;
    }

    @Override
    public User fetch(String authCode) {
        NaverToken tokenInfo = naverApiClient.fetchToken((tokenRequestParams(authCode)));
        NaverMemberResponse naverMemberResponse = naverApiClient.fetchMember("Bearer " + tokenInfo.accessToken());
        return naverMemberResponse.toDomain();
    }

    private MultiValueMap<String, String> tokenRequestParams(String authCode) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", naverOauthConfig.clientId());
        params.add("client_secret", naverOauthConfig.clientSecret());
        params.add("code", authCode);
        params.add("state", naverOauthConfig.state());
        return params;
    }

}