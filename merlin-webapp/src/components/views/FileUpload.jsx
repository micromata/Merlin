import React from 'react';
import {connect} from 'react-redux';
import {PageHeader} from 'react-bootstrap';
import DropArea from '../general/droparea/Component';
import {uploadFile} from '../../actions';

const FileUploadView = (props) => (
    <div>
        <PageHeader>
            File Upload View
        </PageHeader>
        <DropArea
            upload={props.uploadFile}
        />
    </div>
);

const mapStateToProps = () => ({});

const actions = {
    uploadFile
};

export default connect(mapStateToProps, actions)(FileUploadView);
