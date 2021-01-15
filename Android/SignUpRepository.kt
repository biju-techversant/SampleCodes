package com.techversant.eveara.ui.signup

import androidx.lifecycle.MutableLiveData
import com.techversant.eveara.model.request.UserSignUpRequest
import com.techversant.eveara.utils.CodeSnippetExtension
import com.techversant.eveara.webservice.ApiAuthenticationServices
import com.techversant.eveara.webservice.ApiServices
import com.techversant.eveara.webservice.base.NetworkResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SignUpRepository(codeSnippet: CodeSnippetExtension) {
    private val codeSnippetExt: CodeSnippetExtension = codeSnippet
    private val uiDispatcher: CoroutineDispatcher = Dispatchers.IO
    private val apiServices: ApiServices by lazy { ApiServices() }
    private val apiAuthenticationServices: ApiAuthenticationServices by lazy { ApiAuthenticationServices() }
    /**
     * sign up api request
     * @param socialLoginRequest Request with accessToken
     * @param response API response
     */
    suspend fun signUpUser(
        userSignUpRequest: UserSignUpRequest,
        response: MutableLiveData<NetworkResult<Any>?>
    ) {
        GlobalScope.launch(uiDispatcher)
        {
            if (codeSnippetExt.isNetworkActive()) {
                response.postValue(apiAuthenticationServices.signUpUser(userSignUpRequest))
            } else {
                response.postValue(NetworkResult.NoConnectionFailure)
            }

        }
    }

    suspend fun getCountryList(
        accessToken: String?,
        response: MutableLiveData<NetworkResult<Any>?>
    ) {
        GlobalScope.launch(uiDispatcher)
        {
            if (codeSnippetExt.isNetworkActive()) {
                response.postValue(apiServices.getCountryList(accessToken))
            } else {
                response.postValue(NetworkResult.NoConnectionFailure)
            }

        }
    }

    suspend fun getStateList(
        accessToken: String?,
        iso: String,
        response: MutableLiveData<NetworkResult<Any>?>
    ) {
        GlobalScope.launch(uiDispatcher)
        {
            if (codeSnippetExt.isNetworkActive()) {
                response.postValue(apiServices.getCountryStateList(accessToken, iso))
            } else {
                response.postValue(NetworkResult.NoConnectionFailure)
            }

        }
    }

    suspend fun getLanguageList(
        accessToken: String?,
        response: MutableLiveData<NetworkResult<Any>?>
    ) {
        GlobalScope.launch(uiDispatcher)
        {
            if (codeSnippetExt.isNetworkActive()) {
                response.postValue(apiServices.getLanguageList(accessToken))
            } else {
                response.postValue(NetworkResult.NoConnectionFailure)
            }

        }
    }
}