import React from 'react';
import {getRestServiceUrl} from "../../../actions/global";

class DirectoryItem extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            fileBrowserResult: props.item.directory
        }
        this.handleDirectoryChange = this.handleDirectoryChange.bind(this);
        this.handleRecursiveFlagChange = this.handleRecursiveFlagChange.bind(this);
        this.onClickRemove = this.onClickRemove.bind(this);
    }

    handleDirectoryChange = event => {
        this.props.onDirectoryChange(this.props.index, event.target.value);
    }

    handleRecursiveFlagChange = event => {
        this.props.onRecursiveFlagChange(this.props.index, event.target.checked);
    }

    onClickRemove() {
        var index = parseInt(this.props.index, 10);
        this.props.removeItem(index);
    }

    browseDirectory = () => {
        const current = this.state.fileBrowserResult ? "&current=" + encodeURIComponent(this.state.fileBrowserResult) : '';
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
                    this.setState({fileBrowserResult: data.directory})
                    this.props.onDirectoryChange(this.props.index, data.directory)
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
                           onChange={this.handleDirectoryChange}
                           value={this.props.item.directory} placeholder="Enter directory"/>
                </div>
                <div className="form-check col-sm-2">
                    <input className="form-check-input" type="checkbox" checked={this.props.item.recursive}
                           id={"checkedRecursive" + index} name="recursive" onChange={this.handleRecursiveFlagChange}
                           title="If checked, Merlin will search for all templates inside this directory including all sub directories. If not checked, the sub directories will be skipped."/>
                    <label className="form-check-label" htmlFor={"checkedRecursive" + index}>
                        recursive
                    </label>
                </div>
                <div className="col-sm-2">
                    <button type="button" className="btn" onClick={this.browseDirectory}
                            title="Call rest service for browsing local directories">Browse
                    </button>
                    <button type="button" onClick={this.onClickRemove} className="btn btn-danger"
                            title="remove this item"><span
                        className="glyphicon glyphicon-remove"/>
                    </button>
                </div>
            </div>
        );
    }
}

export default DirectoryItem;

