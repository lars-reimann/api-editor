import React from 'react';
import {Nav, Navbar, NavDropdown} from "react-bootstrap";
import {NavLink} from "react-router-dom";
import {useLocation} from "react-router";
import MenuCSS from "./Menu.module.css";
import classNames from "classnames";

export default function Menu(): JSX.Element {

    const pathname = useLocation().pathname.split("/");
    const cssClasses = classNames(MenuCSS.menu, "justify-content-between");

    return (
        <Navbar className={cssClasses} bg="light" expand="lg">
            <Navbar.Text>{
                pathname.slice(1).map((x, i)=>(
                    <React.Fragment key={i}>
                        <span> / </span>
                        <NavLink className={MenuCSS.breadcrumbLink} to={`/${pathname.slice(1, i + 2).join("/")}`}>{x}</NavLink>
                    </React.Fragment>
                ))}
            </Navbar.Text>
            <Nav>
               <NavDropdown title="Import" id="import-dropdown" align="end">
                   <NavDropdown.Item href="#">Python Package</NavDropdown.Item>
                   <NavDropdown.Item href="#">Annotation File</NavDropdown.Item>
               </NavDropdown>
               <Navbar.Text>Export</Navbar.Text>
            </Nav>
        </Navbar>
    );
}
