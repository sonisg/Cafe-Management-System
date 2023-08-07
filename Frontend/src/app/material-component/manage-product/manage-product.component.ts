import { Component, OnInit } from '@angular/core';
import { MatDialogConfig, MatDialog } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { NgxUiLoaderService } from 'ngx-ui-loader';
import { ProductService } from 'src/app/services/product.service';
import { SnackbarService } from 'src/app/services/snackbar.service';
import { GlobalConstants } from 'src/app/shared/global-constants';
import { ProductComponent } from '../dialog/product/product.component';
import { ConfigurationComponent } from '../dialog/configuration/configuration.component';

@Component({
  selector: 'app-manage-product',
  templateUrl: './manage-product.component.html',
  styleUrls: ['./manage-product.component.scss']
})
export class ManageProductComponent implements OnInit {

  displayedColumns: string[] =['name', 'categoryName', 'description', 'price', 'edit'];
  dataSource: any;
  length: any;
  responseMessage: any;
  snackbarService: any;
  dialogRef: any;

  constructor(
    private productService: ProductService,
    private ngxService: NgxUiLoaderService,
    private snackBarService: SnackbarService,
    private dialogConfig: MatDialogConfig,
    private router: Router,
    private dialog: MatDialog,
  ) { }

  ngOnInit(): void {
    this.ngxService.start();
    this.tableData();
  }

  tableData() {
    this.productService.getProducts().subscribe(
      (response: any) => {
        this.ngxService.stop();
        this.dataSource = new MatTableDataSource(response);
      },
      (error: any) => {
        this.ngxService.stop();
        if (error.error?.message) {
          this.responseMessage = error.error?.message;
        } else {
          this.responseMessage = GlobalConstants.error;
        }
        this.snackBarService.openSnackBar(
          this.responseMessage,
          GlobalConstants.error
        );
      }
    );
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  handleAddAction() {
    const dialog = new MatDialogConfig();
    dialog.data={
      action : 'Add'
    };
    dialog.width ="850px";
    const dialogRef = this.dialog.open(ProductComponent, dialog);
    this.router.events.subscribe(()=>{
      dialogRef.close();
    });
    const sub = dialogRef.componentInstance.onAddProduct.subscribe((response)=>{
      this.tableData();
    })
  }

  handleEditAction(values: any) {
    const dialog = new MatDialogConfig();
    dialog.data={
      action : 'Edit',
      data: values
    };
    dialog.width ="850px";
    const dialogRef = this.dialog.open(ProductComponent, dialog);
    this.router.events.subscribe(()=>{
      dialogRef.close();
    });
    const sub = dialogRef.componentInstance.onEditProduct.subscribe((response)=>{
      this.tableData();
    })
  }

  handleDeleteAction(values: any){
    const dialog = new MatDialogConfig();
    dialog.data={
      message : 'delete ' + values.name + ' product?',
      confirmation: true
    };

    const dialogRef = this.dialog.open(ConfigurationComponent, dialog);
    const sub = dialogRef.componentInstance.onEmitStatusChange.subscribe((response)=>{
      this.ngxService.start();
      this.deleteProduct(values.id);
      dialogRef.close();
    })
  }
  deleteProduct(id: any) {
    this.productService.delete(id).subscribe((response:any)=>{
      this.ngxService.stop();
      this.tableData()
      this.responseMessage = response.message;
      this.snackbarService.openSnackBar(this.responseMessage, 'Success');
    },(error)=>{
      this.ngxService.stop();
      if (error.error?.message) {
        this.responseMessage = error.error?.message;
      } else {
        this.responseMessage = GlobalConstants.error;
      }
      this.snackbarService.openSnackBar(
        this.responseMessage,
        GlobalConstants.error
      );
    });
  }

  onChange(status: any, id: any){
    this.ngxService.start();
    var data = {
      status: status.toString(),
      id: id,
    }
    this.productService.updateStatus(data).subscribe((response:any)=>{
      this.ngxService.stop();
      this.responseMessage = response.message;
      this.snackbarService.openSnackBar(this.responseMessage, 'Success');
    },(error)=>{
      this.ngxService.stop();
      if (error.error?.message) {
        this.responseMessage = error.error?.message;
      } else {
        this.responseMessage = GlobalConstants.error;
      }
      this.snackbarService.openSnackBar(
        this.responseMessage,
        GlobalConstants.error
      );
    });
  }
}
