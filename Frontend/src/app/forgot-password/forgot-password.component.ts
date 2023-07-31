import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { NgxUiLoaderService } from 'ngx-ui-loader';
import { SnackbarService } from '../services/snackbar.service';
import { UserService } from '../services/user.service';
import { SignupComponent } from '../signup/signup.component';
import { GlobalConstants } from '../shared/global-constants';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.scss']
})
export class ForgotPasswordComponent implements OnInit {

  forgotPasswordForm: any = FormGroup;
  responseMessage: any;

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private userService: UserService,
    private snackBarService: SnackbarService,
    private dialogRef: MatDialogRef<ForgotPasswordComponent>,
    private ngxService: NgxUiLoaderService
  ) { }

  ngOnInit(): void {
    this.forgotPasswordForm = this.formBuilder.group({   
      email:[null, [Validators.required, Validators.pattern(GlobalConstants.emailRegex)]],
    })
  }

  handleSubmit(){
    this.ngxService.start();
    var formData = this.forgotPasswordForm.value;
    var data ={
    email: formData.email,
    }
    this.userService.forgotPassword(data).subscribe((response: any) =>{
    this.ngxService.stop();
    this.dialogRef.close();
    this.responseMessage = response ?.message;
    this.snackBarService.openSnackBar(this.responseMessage, "");
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
