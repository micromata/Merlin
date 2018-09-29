import React from 'react';
import {PageHeader} from 'react-bootstrap';
import {getRestServiceUrl} from "../../../actions/global";

class DirectoryEntry extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            directory: this.props.directory,
            recursive: this.props.recursive
        }
        this.handleTextChange = this.handleTextChange.bind(this);
        this.handleCheckboxChange = this.handleCheckboxChange.bind(this);
    }

    handleTextChange = event => {
        event.preventDefault();
        this.setState({[event.target.name]: event.target.value});
    }

    handleCheckboxChange = event => {
        this.setState({[event.target.name]: event.target.checked});
    }


    browseDirectory = () => {
        const current = this.state.directory ? "&current=" + encodeURIComponent(this.state.directory) : '';
        fetch(getRestServiceUrl("files/browse-local-filesystem?type=dir" + current), {
            method: "GET",
            dataType: "JSON",
            headers: {
                "Content-Type": "text/plain; charset=utf-8",
            }
        })
            .then((resp) => {
                return resp.json()
            })
            .then((data) => {
                if (data.directory) {
                    this.setState({directory: data.directory})
                }
            })
            .catch((error) => {
                console.log(error, "Oups, what's happened?")
            })
    }


    render() {
        const index = this.props.index;
        return (
            <div className="form-group row">
                <label className="col-sm-2 col-form-label"
                       htmlFor={"inputDirectory" + index}>Directory</label>
                <div className="col-sm-6">
                    <input name="directory" type="text" className="form-control" id={"inputDirectory" + index}
                           onChange={this.handleTextChange}
                           value={this.state.directory} placeholder="Enter directory"/>
                </div>
                <div className="form-check col-sm-2">
                    <input className="form-check-input" type="checkbox" checked={this.state.recursive}
                           id={"checkedRecursive" + index} name="recursive" onChange={this.handleCheckboxChange}
                           title="If checked, Merlin will search for all templates inside this directory including all sub directories. If not checked, the sub directories will be skipped."/>
                    <label className="form-check-label" htmlFor={"checkedRecursive" + index}>
                        recursive
                    </label>
                </div>
                <div className="col-sm-2">
                    <button type="button" className="btn" onClick={this.browseDirectory}
                            title="Call rest service for browsing local directories">Browse
                    </button>
                    <button type="button" className="btn btn-danger" title="remove this entry"><span
                        className="glyphicon glyphicon-remove"/>
                    </button>
                </div>
            </div>
        );
    }
}

export default DirectoryEntry;

