import React from 'react';
import {Glyphicon} from 'react-bootstrap';
import './style.css';

class DropArea extends React.Component {

    constructor(props) {
        super(props);

        this.state = {
            inDrag: false,
            files: []
        };

        this.handleDragEnter = this.handleDragEnter.bind(this);
        this.handleDragLeave = this.handleDragLeave.bind(this);
        this.cancelAndLog = this.cancelAndLog.bind(this);
        this.handleDropCapture = this.handleDropCapture.bind(this);
        this.handleDragOver = this.handleDragOver.bind(this);
        this.handleInputChange = this.handleInputChange.bind(this);
        this.addFiles = this.addFiles.bind(this);
    }

    handleDragEnter(event) {
        event.preventDefault();

        this.setState({
            inDrag: true
        });
    }

    handleDragLeave(event) {
        event.preventDefault();

        this.setState({
            inDrag: false
        });
    }

    handleDragOver(event) {
        event.preventDefault();
    }

    cancelAndLog(event) {
        event.stopPropagation();
        event.preventDefault();
    }

    handleDropCapture(event) {
        this.handleDragLeave(event);

        this.addFiles(event.dataTransfer.files);
    }

    handleInputChange(event) {
        this.addFiles(event.target.files);
    }

    addFiles(fileList) {
        const files = [];

        if (this.props.multiple) {

            for (let i = 0; i < fileList.length; i++) {
                const file = fileList[i];

                if (this.state.files.filter(cf =>
                    cf.lastModified === file.lastModified &&
                    cf.name === file.name &&
                    cf.size === file.size).length === 0) {
                    files.push(file);
                }
            }

            this.setState({
                files: Array.of(...this.state.files, ...files)
            });

            return;
        }

        this.setState({
            files: [fileList[0]]
        });
    }

    render() {
        const inputArguments = {
            id: this.props.id,
            onChange: this.handleInputChange,
            className: 'file',
            type: 'file',
            name: 'file',
            ref: 'input'
        };

        if (this.props.multiple) {
            inputArguments['name'] = 'files[]';
            inputArguments['data-multiple-caption'] = '{count} files selected.';
            inputArguments['multiple'] = true;
        }

        return (
            <div>
                <div
                    onClick={() => this.refs.input.click()}
                    className={`drop-area ${this.state.inDrag ? 'onDrag' : ''}`}
                >
                    <div
                        onDragEnter={this.handleDragEnter}
                        onDragLeave={this.handleDragLeave}

                        onDragOver={this.handleDragOver}
                        onDropCapture={this.handleDropCapture}
                        ref={'dropzone'}
                        className={'background'}
                    >
                        <form encType={'multipart/form-data'}>
                            <input
                                {...inputArguments}
                            />
                        </form>
                        <span className={'info'}>
                            <Glyphicon glyph={'upload'}/>
                            <b> Select a file,</b> or drop one here.
                        </span>
                    </div>
                </div>
                {this.state.files.map((file, index) => (<p key={index}>{file.name}</p>))}
            </div>
        );
    }

}

export default DropArea;
