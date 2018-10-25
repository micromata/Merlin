import React from 'react';
import {connect} from 'react-redux';
import {PageHeader} from '../general/BootstrapComponents';
import DropArea from '../general/droparea/DropArea';
import {uploadFile} from '../../actions';
import {IconSpinner} from "../general/IconComponents";

const FileUploadView = (props) => (
    <React.Fragment>
        <PageHeader>
            File Upload View
        </PageHeader>
        <DropArea
            upload={props.uploadFile}
        />

        <h3>ToDo</h3>
        <IconSpinner/>
        <ul>
            <li>We should skip the upload button and do the action instantly after dropping a file.</li>
            <li>Support the following upload-results:
                <ol>
                    <li>Response = Text: Display message (e. g.) error message such as "Unsupported file".</li>
                    <li>Response = page: Redirect to a page with pre-filled data: a dialog asking the user what to do: Run a Template by choosing the matching templates, create a new one etc.</li>
                    <li>Response = file: Download the returned file (zip, doc, xls etc.). This is used, if Merlin auto detects a run file and returns the result file directly</li>
                </ol>
            </li>
        </ul>

    </React.Fragment>
);

const mapStateToProps = () => ({});

const actions = {
    uploadFile
};

export default connect(mapStateToProps, actions)(FileUploadView);
