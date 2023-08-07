import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { NgxUiLoaderService } from 'ngx-ui-loader';
import { ForgotPasswordComponent } from 'src/app/forgot-password/forgot-password.component';
import { SnackbarService } from 'src/app/services/snackbar.service';
import { UserService } from 'src/app/services/user.service';
import { GlobalConstants } from 'src/app/shared/global-constants';

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.scss']
})
export class ChangePasswordComponent implements OnInit {

  oldPassword = true;
  newPassword = true;
  confirmPassword = true;
  changePasswordForm: any = FormGroup;
  responseMessage: any;
  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private userService: UserService,
    private snackBarService: SnackbarService,
    private dialogRef: MatDialogRef<ForgotPasswordComponent>,
    private ngxService: NgxUiLoaderService
  )
   { }

  ngOnInit(): void {
    this.changePasswordForm = this.formBuilder.group({
      oldPassword: [null, [Validators.required]],
      newPassword: [null, [Validators.required]],
      confirmPassword: [null, [Validators.required]],
    })
  }

  validateSubmit(){
    if(this.changePasswordForm.controls['newPassword'].value != this.changePasswordForm.controls['confirmPassword'].value){
      return true;
    }else{
      return false;
    }

  }

  handleSubmit(){
    this.ngxService.start();
    var formData = this.changePasswordForm.value;
    var data ={
    oldPassword: formData.oldPassword,
    newPassword: formData.newPassword,
    confirmPassword: formData.confirmPassword,
    }
    this.userService.changePassword(data).subscribe((response: any) =>{
    this.ngxService.stop();
    this.dialogRef.close();
    this.responseMessage = response ?.message;
    this.snackBarService.openSnackBar(this.responseMessage, "success");
    this.router.navigate(['/']);
  },(error)=>{
    this.ngxService.stop();
    if(error.error?.message){
      this.responseMessage = error.error?.message;
    }else{
      this.responseMessage = GlobalConstants.error;
    }
    this.snackBarService.openSnackBar(this.responseMessage, GlobalConstants.error);
  })
  }

}
