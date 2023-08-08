import { Injectable } from '@angular/core';
import { AuthService } from './auth.service';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { SnackbarService } from './snackbar.service';
import jwt_decode from "jwt-decode";
import { GlobalConstants } from '../shared/global-constants';

@Injectable({
  providedIn: 'root'
})
export class RouteGuardService {

  constructor(
    private auth: AuthService,
    public router: Router,
    public snackBar: SnackbarService) { 

    }

    canActivate(route: ActivatedRouteSnapshot): boolean{
      let expectedRoleArray = route.data;
      expectedRoleArray= expectedRoleArray.expectedRole;
      const token: any = localStorage.getItem('token');
      var tokenPayload: any;
      try{
        tokenPayload = jwt_decode(token);
      }catch(err){
        localStorage.clear();
        this.router.navigate(['/']);
      }
      let expectedRole = '';
      for(let i=0; i<expectedRoleArray.length;i++){
        if(expectedRoleArray[i]==tokenPayload.user){
          expectedRole=tokenPayload.user;
        }
      }

      if(tokenPayload.user == 'user' || tokenPayload.user == 'admin'){
        if(this.auth.isAuthenicated() && tokenPayload.user == expectedRole){
          return true;
        }
        this.snackBar.openSnackBar(GlobalConstants.unauthorized, GlobalConstants.error);
        this.router.navigate(['/cafe/dashboard']);
        return false;
      }
      else{
        this.router.navigate(['/']);
        localStorage.clear();
        return false;
      }

    }
}
