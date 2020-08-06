import { Component } from '@angular/core';
import { ApiService } from '../api.service';
import { takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { UploadRequest } from '../app-models/UploadRequest';


@Component({
  selector: 'app-root',
  templateUrl: './image.component.html',
  styleUrls: ['./image.component.css']
})
export class ImageComponent {
  inputInfo: UploadRequest = new UploadRequest();
  loading: boolean = false;
  message: string;
  messageType: string;

  destroy$: Subject<boolean> = new Subject<boolean>();

  constructor(
    private apiService: ApiService
  ) { }


  onFileChanged(event) {
    this.inputInfo.selectedImage = event.target.files[0]
  }

  onUpload() {
    this.message = '';
    if (!this.validateImage()) {
      return;
    }
    this.loading = true;
    this.apiService.saveImage(this.inputInfo)
      .pipe(takeUntil(this.destroy$)).subscribe((res) => {
        this.setMessage('Image is uploaded successfully', 'success');
        this.inputInfo = new UploadRequest();
      }, (error) => {
        this.setMessage('Image upload is failed ' + error, this.messageType = 'error');
      }).add(() => {
        this.loading = false;
      });
  }

  setMessage(message:string, messageType: string) {
    this.message = message;
    this.messageType = messageType;
  }

  validateImage() {
    let valid:boolean = true;
    let imageName:string = this.inputInfo.selectedImage.name.toLowerCase();
    if (!this.inputInfo.description) {
      this.setMessage('Description is needed', 'error');
      valid = false;
    } else if (!imageName.endsWith('png') && !imageName.endsWith('jpeg')) {
      this.setMessage('Only PNG and JPEG images are allowed', 'error');
      valid = false;
    } else if (this.inputInfo.selectedImage.size > 500000) {
      this.setMessage('Max allowed image size is 500KB', 'error');
      valid = false;
    }

    return valid;
  }
}
