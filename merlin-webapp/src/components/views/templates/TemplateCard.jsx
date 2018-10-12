import React from 'react';

class TemplateCard extends React.Component {
    render = () => {

        return <div className="col-sm-6">
            <div className="card  border-success mb-3">
                <div className="card-hedader">
                    {this.props.id}
                </div>
                <div className="card-body">
                    <h5 className="card-title"></h5>
                    <p className="card-text">{this.props.description}.</p>
                    <footer class="blockquote-footer">Click to run.</footer>
                </div>
            </div>
        </div>;
    };

    constructor(props) {
        super(props);
    }
}

export default TemplateCard;