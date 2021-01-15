//
//  LoginVC.swift
//  Eveara
//
//  Created by FCI-MA865 on 11/05/20.
//  Copyright © 2020 FCI-MA865. All rights reserved.
//

import UIKit

class LoginVC: UITableViewController {

    @IBOutlet weak var viewUsernameHolder: UIView!
    @IBOutlet weak var viewPasswordHolder: UIView!
    @IBOutlet weak var txtFldUsername: UITextField! {
        didSet{
            txtFldUsername.text = "rp@eveara.com"
        }
    }
    @IBOutlet weak var txtFldPassword: UITextField! {
        didSet {
            txtFldPassword.text = "evearaeveara"
        }
    }
    
    var delegate: SocialAuth?

    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.navigationController?.navigationBar.isHidden = true
        self.tableView.backgroundImage = #imageLiteral(resourceName: "bg_Image")
        self.tableView.backgroundImageAlpha = 0.4
        
    }
    
    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        viewUsernameHolder.roundCorners([.topLeft, .topRight], radius: 15.0)
        viewPasswordHolder.roundCorners([.bottomLeft, .bottomRight], radius: 15.0)
    }
    
    func setRootViewController()  {
        let storyboard = UIStoryboard(name: "Tabbar", bundle: nil)
        let viewController = storyboard.instantiateViewController(withIdentifier: "TabBarController") as! TabBarController
        (UIApplication.shared.delegate as! AppDelegate).window?.rootViewController = viewController
    }
    
    //MARK:- Button Action
    @IBAction func btnActionSignIn(_ sender: UIButton) {
        guard let username = txtFldUsername.text, !username.isEmpty else {
            alert(message: "Please enter Email Address", title: "Mandatory fields")
            return
        }
        if !username.isValidEmail {
            alert(message: "Please provide a valid Email Address", title: "Email Validation")
            return 
        }
        guard let password = txtFldPassword.text, !password.isEmpty else {
            alert(message: "Please enter password", title: "Mandatory fields")
            return
        }
        self.showHUD(message: "")
        APIClient().perform(GetToken<EmailLoginBody>(queryParams: nil,
                                                     body: EmailLoginBody(
                                                        grant_type: "password",
                                                        username: username,
                                                        password: password)
        )) { (result) in
            self.hideHUD()
            switch result {
            case .success(let value):
                if let accessToken = value.access_token, let refreshToken = value.refresh_token, let tokenType = value.token_type {
                    let keyChainData =  ["accessToken": accessToken,
                                                "refreshToken": refreshToken,
                                                "tokenType": tokenType]
                    KeychainWrapper().saveData(data: keyChainData, for: "UserToken")
                    Environment.token = Token(value)
                    DispatchQueue.main.async {
                        self.setRootViewController()
                    }
                }
            case .failure(let error):
                DispatchQueue.main.async {
                    switch error as? EVError {
                    case let .otpVarification(otpUuid, _):
                        //print(otpUuid, message)
                        let storyboard = UIStoryboard(name: "Main", bundle: nil)
                        let signUpVarification = storyboard.instantiateViewController(withIdentifier: "SignUpVerificationVC") as! SignUpVerificationVC
                        signUpVarification.uuidString = otpUuid
                        self.navigationController?.pushViewController(signUpVarification, animated: false)
                    default:
                        self.alert(message: error.localizedDescription, title: "Exception")
                    }
                }
            }
        }
    }
    
    @IBAction func btnActionFBLogin(_ sender: UIButton) {
        self.showHUD(message: "")
        let authentication = AuthenticationProvider.facebook
        authentication.authenticate(self)
    }
    
        
    @IBAction func btnActionGoogleLogin(_ sender: UIButton) {
        self.showHUD(message: "")
        let authentication = AuthenticationProvider.Google
        authentication.authenticate(self)
    }
    
    @IBAction func btnActionSpotify(_ sender: UIButton) {
        
        self.showHUD(message: "")
        let authentication = AuthenticationProvider.Spotify
        authentication.authenticate(self)
    }
    
}

extension LoginVC:UITextFieldDelegate{
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        return textField.resignFirstResponder()
    }
}

extension LoginVC: AuthenticationProviderDelegate {
    func didSuccess(authenticationProvider: AuthenticationProvider) {
        self.hideHUD()
        DispatchQueue.main.async {
            self.setRootViewController()
        }
        
    }
    
    func authenticationProvider(_ authenticationProvider: AuthenticationProvider, error: Error) {
        self.hideHUD()
        switch authenticationProvider {
        case .Spotify:
            DispatchQueue.main.async {
                self.alert(message: "Spotify app not found", title: "Spotify sdk error")
            }
        default:
            DispatchQueue.main.async {
                self.alert(message: error.localizedDescription, title: "Error")
            }
        }
    }
    
    func parentViewController(for authenticationProvider: AuthenticationProvider) -> UIViewController {
        return self
    }
}



