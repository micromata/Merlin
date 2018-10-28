import React from 'react';
import {Button} from 'reactstrap';
import {IconDownload} from '../../general/IconComponents';
import {getResponseHeaderFilename, getRestServiceUrl} from '../../../utilities/global';
import DropArea from '../../general/droparea/DropArea';
import downloadFile from '../../../utilities/download';
import {uploadFile} from '../../../actions';
import connect from 'react-redux/es/connect/connect';
import LoadingOverlay from "../../general/loading/LoadingOverlay";
import I18n from "../../general/translation/I18n";

class TemplateSerialRunTab extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            loading: false
        };
        this.uploadFile = this.uploadFile.bind(this);
    }

    uploadFile(file) {
        this.setState({loading: true});
        const formData = new FormData();
        formData.append('file', file);
        let filename;
        return fetch(getRestServiceUrl('files/upload'), {
            method: 'POST',
            body: formData
        })
            .then(response => {
                this.setState({loading: false});
                if (!response.ok) {
                    return response.text().then(text => {
                        throw new Error(text)
                    });
                }
                filename = getResponseHeaderFilename(response.headers.get('Content-Disposition'));
                return response.blob();
            })
            .then(blob => downloadFile(blob, filename))
            .catch(alert);
    }

    getSerialTemplate = () => {
        let filename;
        fetch(getRestServiceUrl('templates/get-serial-run-excel', {
            templatePrimaryKey: this.props.templatePrimaryKey,
            templateDefinitionPrimaryKey: this.props.templateDefinitionPrimaryKey
        }), {
            method: 'GET',
            headers: {
                "Content-Type": "text/plain; charset=utf-8"
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(response.statusText);
                }

                filename = getResponseHeaderFilename(response.headers.get('Content-Disposition'));
                return response.blob();
            })
            .then(blob => downloadFile(blob, filename))
            .catch(alert);
    };

    render() {
        return (
            <React.Fragment>
                <Button className={'btn-outline-primary'} onClick={this.getSerialTemplate}>
                    <IconDownload/><I18n name={'templates.serialRun.button.exportExcelTemplate'} />
                </Button>
                <h4><I18n name={'templates.serialRun.upload.title'} /></h4>
                <DropArea upload={this.uploadFile}><I18n name={'common.droparea.title'}/></DropArea>
                {this.state.loading ? <LoadingOverlay/> : ''}
            </React.Fragment>
        );
    }
}

const mapStateToProps = () => ({});

const actions = {
    uploadFile
};

export default connect(mapStateToProps, actions)(TemplateSerialRunTab);
